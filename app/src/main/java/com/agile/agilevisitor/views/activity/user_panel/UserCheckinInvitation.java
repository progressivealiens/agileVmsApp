package com.agile.agilevisitor.views.activity.user_panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyButton;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.customviews.customSpinner;
import com.agile.agilevisitor.helper.CheckNetworkConnection;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.ProgressView;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.helper.Validation;
import com.agile.agilevisitor.webapi.ApiClient;
import com.agile.agilevisitor.webapi.ApiInterface;
import com.agile.agilevisitor.webapi.ApiResponse;
import com.agile.agilevisitor.webapi.OTPResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCheckinInvitation extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.root_checkin_invitation)
    LinearLayout rootCheckinInvitation;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.lin_toolbar)
    LinearLayout linToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_user_name)
    TextInputEditText etUserName;
    @BindView(R.id.et_user_number)
    TextInputEditText etUserNumber;
    @BindView(R.id.iv_contacts)
    ImageView ivContacts;
    @BindView(R.id.et_user_email)
    TextInputEditText etUserEmail;
    @BindView(R.id.ti_date)
    TextInputLayout tiDate;
    @BindView(R.id.et_date)
    TextInputEditText etDate;
    @BindView(R.id.et_time)
    TextInputEditText etTime;
    @BindView(R.id.spinner_visitor_type)
    customSpinner spinnerVisitorType;
    @BindView(R.id.btn_send_invite)
    MyButton btnSendInvite;

    public static final int PICK_CONTACT = 1231;

    List<String> visitorTypeList;
    String selectedVisitorType = "";

    ApiInterface apiInterface,apiInterfaceSMS;
    ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_checkin_invitation);
        ButterKnife.bind(this);

        initialize();

        setDataForSpinner();

        spinnerVisitorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVisitorType = visitorTypeList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setDataForSpinner() {
        visitorTypeList.add("Visitor");
        visitorTypeList.add("Vendor");
        visitorTypeList.add("Employee");
        visitorTypeList.add("Candidate");

        spinnerVisitorType.setAdapter(new VisitorTypeAdapter(UserCheckinInvitation.this, visitorTypeList));
    }

    private void initialize() {
        setSupportActionBar(toolbar);
        linToolbar.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Send Invitation");

        ivBack.setOnClickListener(this);
        etDate.setOnClickListener(this);
        etTime.setOnClickListener(this);
        ivContacts.setOnClickListener(this);
        btnSendInvite.setOnClickListener(this);

        visitorTypeList = new ArrayList<>();
        spinnerVisitorType.setPrompt("Select Visitor Type *");

        apiInterface = ApiClient.getClient(UserCheckinInvitation.this, 0).create(ApiInterface.class);
        apiInterfaceSMS=ApiClient.getOTPClient(UserCheckinInvitation.this).create(ApiInterface.class);
        progressView = new ProgressView(UserCheckinInvitation.this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.et_date:
                Utils.oldDatePicker(UserCheckinInvitation.this, etDate);
                break;
            case R.id.et_time:
                Utils.timePicker(UserCheckinInvitation.this, etTime);
                break;
            case R.id.iv_contacts:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
                break;
            case R.id.btn_send_invite:

                if (Validation.nullValidator(etUserName.getText().toString())) {
                    Toast.makeText(this, "Please Fill The Name", Toast.LENGTH_LONG).show();
                } else if (Validation.nullValidator(etUserNumber.getText().toString())) {
                    Toast.makeText(this, "Please Fill The Number", Toast.LENGTH_LONG).show();
                } else if (Validation.nullValidator(etDate.getText().toString())) {
                    Toast.makeText(this, "Please Fill The Date", Toast.LENGTH_LONG).show();
                } else if (Validation.nullValidator(etTime.getText().toString())) {
                    Toast.makeText(this, "Please Fill The Time", Toast.LENGTH_LONG).show();
                } else if (selectedVisitorType.equalsIgnoreCase("")) {
                    Toast.makeText(this, "Please Select The Visitor Type", Toast.LENGTH_SHORT).show();
                } else {
                    connectApiToInvite(
                            etUserName.getText().toString(),
                            etUserNumber.getText().toString(),
                            etUserEmail.getText().toString(),
                            etDate.getText().toString(),
                            etTime.getText().toString(),
                            selectedVisitorType);
                }

                break;
        }
    }

    private void connectApiToInvite(String name, String number, String email, String date, String time, String visitorType) {
        if (CheckNetworkConnection.isConnection1(UserCheckinInvitation.this, true)) {
            progressView.showLoader();

            Call<ApiResponse> call = apiInterface.sendInvitation(
                    PrefData.readStringPref(PrefData.pref_fcm_token),
                    name,
                    number,
                    email,
                    date,
                    time,
                    visitorType
            );
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {
                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            String address=response.body().getAddress();
                            String passcode=String.valueOf(response.body().getPasscode());
                            String QrLink=response.body().getQrLink();

                            connectApiToSendSMSInvitation(name,number,address,date,time,passcode,QrLink);

                        } else {
                            Utils.showSnackBar(rootCheckinInvitation, response.body().getMsg(), UserCheckinInvitation.this);
                        }
                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(UserCheckinInvitation.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(UserCheckinInvitation.this, getString(R.string.network_busy), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(UserCheckinInvitation.this, getString(R.string.resource_not_found), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserCheckinInvitation.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    progressView.hideLoader();
                }
            });
        }
    }

    private void connectApiToSendSMSInvitation(String Name,String Number,String Address,String Date,String time,String passcode,String QrLink) {

        if (CheckNetworkConnection.isConnection1(UserCheckinInvitation.this, true)) {
            progressView.showLoader();

            Call<OTPResponse> call = apiInterfaceSMS.sendInvitationOnMobile("AgileI",
                    Number,
                    "AgileInvitation",
                    Name,
                    PrefData.readStringPref(PrefData.pref_user_name),
                    Address,
                    Date,
                    time,
                    passcode,
                    QrLink
            );


            call.enqueue(new Callback<OTPResponse>() {
                @Override
                public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {


                            int noOfInvites=Integer.parseInt(PrefData.readStringPref(PrefData.pref_user_invites));
                            noOfInvites=noOfInvites+1;
                            PrefData.writeStringPref(PrefData.pref_user_invites,noOfInvites+"");

                            Toast.makeText(UserCheckinInvitation.this, "Invitation send successfully along with Passcode and Qr code", Toast.LENGTH_LONG).show();

                            Intent intent=new Intent(UserCheckinInvitation.this,UserHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(UserCheckinInvitation.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(UserCheckinInvitation.this, getString(R.string.network_busy), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(UserCheckinInvitation.this, getString(R.string.resource_not_found), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserCheckinInvitation.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<OTPResponse> call, Throwable t) {
                    progressView.hideLoader();
                }
            });

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {

            case (PICK_CONTACT):

                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        try {

                            if (hasPhone.equalsIgnoreCase("1")) {
                                Cursor phones = getContentResolver().query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                        null, null);

                                phones.moveToFirst();
                                String cNumber = phones.getString(phones.getColumnIndex("data1"));

                                String mobNumber=cNumber.replaceAll("\\D+","").replaceAll("[\\s]+","").trim();

                                if (TextUtils.isEmpty(mobNumber)){
                                    Toast.makeText(this, "No Number is attached with this contact", Toast.LENGTH_SHORT).show();
                                } else if (mobNumber.length()<=10){
                                    etUserNumber.setText(mobNumber);
                                }else {
                                    etUserNumber.setText(mobNumber.substring(mobNumber.length() - 10));
                                }
                            }

                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            etUserName.setText(name);
                        } catch (Exception ex) {
                            ex.getMessage();
                        }
                    }
                }
                break;
        }
    }

    public class VisitorTypeAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflter;
        List<String> visitorType;

        public VisitorTypeAdapter(Context context, List<String> visitorType) {
            this.context = context;
            this.visitorType = visitorType;
            inflter = (LayoutInflater.from(context));
        }

        @Override
        public int getCount() {
            return visitorType.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflter.inflate(R.layout.spinner_layout, null);
            TextView names = convertView.findViewById(R.id.value);
            names.setText(visitorType.get(position));

            return convertView;
        }
    }

}
