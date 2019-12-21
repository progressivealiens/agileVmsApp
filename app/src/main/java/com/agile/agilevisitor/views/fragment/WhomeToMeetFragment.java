package com.agile.agilevisitor.views.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyButton;
import com.agile.agilevisitor.customviews.customSpinner;
import com.agile.agilevisitor.helper.CheckNetworkConnection;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.ProgressView;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.views.activity.visiting_panel.AllDetailsActivity;
import com.agile.agilevisitor.views.activity.visiting_panel.ConfirmVisitorDetails;
import com.agile.agilevisitor.views.activity.visiting_panel.ConfirmVisitorRestDetailsActivity;
import com.agile.agilevisitor.webapi.ApiClient;
import com.agile.agilevisitor.webapi.ApiInterface;
import com.agile.agilevisitor.webapi.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WhomeToMeetFragment extends Fragment implements View.OnClickListener {

    View v;

    @BindView(R.id.spinner_company)
    customSpinner spinnerCompany;
    @BindView(R.id.spinner_name)
    customSpinner spinnerName;
    @BindView(R.id.btn_wtm_submit)
    MyButton btnWtmSubmit;
    @BindView(R.id.root_wtm)
    LinearLayout rootWtm;

    List<ApiResponse.SubUnitListBean> companyDetailsList;
    List<ApiResponse.PersonsListBean> employeeDetailsList;

    ApiInterface apiInterface;
    ProgressView progressView;

    String subunitId="";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_whome_to_meet, container, false);
        ButterKnife.bind(this, v);

        initialize();

        getDataForSpinnerCompany();

        spinnerCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (PrefData.readBooleanPref(PrefData.pref_called_from_confirm_fragment)){
                    ConfirmVisitorDetails.subUnitUniqueId=String.valueOf(companyDetailsList.get(position).getSubUnitId());
                    PrefData.writeStringPref(PrefData.pref_sub_unit_name,companyDetailsList.get(position).getSubUnitNameX());
                }else{
                    AllDetailsActivity.subUnitId=String.valueOf(companyDetailsList.get(position).getSubUnitId());
                    PrefData.writeStringPref(PrefData.pref_sub_unit_name,companyDetailsList.get(position).getSubUnitNameX());
                }

                getDataForSpinnerEmployee();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (PrefData.readBooleanPref(PrefData.pref_called_from_confirm_fragment)){
                    ConfirmVisitorDetails.empId=String.valueOf(employeeDetailsList.get(position).getPersonId());
                    PrefData.writeStringPref(PrefData.pref_employee_name,employeeDetailsList.get(position).getPersonName());
                }else{
                    AllDetailsActivity.employeeId=String.valueOf(employeeDetailsList.get(position).getPersonId());
                    PrefData.writeStringPref(PrefData.pref_employee_name,employeeDetailsList.get(position).getPersonName());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    private void initialize() {
        if (PrefData.readBooleanPref(PrefData.pref_called_from_confirm_fragment)){
            ConfirmVisitorRestDetailsActivity.confirmTitle.setText("Whome To Meet");
        }else{
            AllDetailsActivity.tvTitle.setText("Whome To Meet");
        }


        apiInterface = ApiClient.getClient(getActivity(),0).create(ApiInterface.class);
        progressView = new ProgressView(getActivity());

        companyDetailsList = new ArrayList<>();
        employeeDetailsList=new ArrayList<>();

        spinnerCompany.setPrompt("Select Company");
        spinnerName.setPrompt("Select Name");

        btnWtmSubmit.setOnClickListener(this);
    }

    private void getDataForSpinnerCompany() {

        if (CheckNetworkConnection.isConnection1(getActivity(), true)) {
            progressView.showLoader();

            Call<ApiResponse> call = apiInterface.getAllSubUnit(PrefData.readStringPref(PrefData.pref_fcm_token));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            companyDetailsList.clear();
                            companyDetailsList.addAll(response.body().getSubUnitList());

                            spinnerCompany.setAdapter(new companyAdapter(getActivity(), companyDetailsList));

                        } else {
                            Utils.showSnackBar(rootWtm, response.body().getMsg(), getActivity());
                        }
                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(getActivity(), getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(getActivity(), getString(R.string.network_busy), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(getActivity(), getString(R.string.resource_not_found), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    progressView.hideLoader();
                    t.printStackTrace();
                }
            });

        }
    }

    private void getDataForSpinnerEmployee() {

        if (CheckNetworkConnection.isConnection1(getActivity(), true)) {
            progressView.showLoader();

            if (PrefData.readBooleanPref(PrefData.pref_called_from_confirm_fragment)){
                subunitId=ConfirmVisitorDetails.subUnitUniqueId;
            }else{
                subunitId=AllDetailsActivity.subUnitId;
            }


            Call<ApiResponse> call = apiInterface.getAllSubUnitPerson(subunitId);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            employeeDetailsList.clear();
                            employeeDetailsList.addAll(response.body().getPersonsList());

                            spinnerName.setAdapter(new EmployeeAdapter(getActivity(), employeeDetailsList));

                        } else {
                            Utils.showSnackBar(rootWtm, response.body().getMsg(), getActivity());
                        }
                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(getActivity(), getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(getActivity(), getString(R.string.network_busy), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(getActivity(), getString(R.string.resource_not_found), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    progressView.hideLoader();
                    t.printStackTrace();
                }
            });

        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_wtm_submit) {

            if (PrefData.readBooleanPref(PrefData.pref_called_from_confirm_fragment)){

                if (ConfirmVisitorDetails.subUnitUniqueId.equalsIgnoreCase("")){
                    Utils.showSnackBar(rootWtm,"Please Select Company",getActivity());
                }else if (ConfirmVisitorDetails.empId.equalsIgnoreCase("")){
                    Utils.showSnackBar(rootWtm,"Please Select Employee Name",getActivity());
                }else{
                    Utils.changeFragmentConfirmVisitor(new DisclaimerFragment(), "frag_confirm_disclaimer", ConfirmVisitorRestDetailsActivity.confirmActivity);
                }

            }else{

                if (AllDetailsActivity.subUnitId.equalsIgnoreCase("")){
                    Utils.showSnackBar(rootWtm,"Please Select Company",getActivity());
                }else if (AllDetailsActivity.employeeId.equalsIgnoreCase("")){
                    Utils.showSnackBar(rootWtm,"Please Select Employee Name",getActivity());
                }else{
                    Utils.changeFragmentAllDetails(new DisclaimerFragment(), "frag_disclaimer", AllDetailsActivity.activity);
                }


            }
        }
    }


    public class companyAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflter;
        List<ApiResponse.SubUnitListBean> companyDetails;

        public companyAdapter(Context context, List<ApiResponse.SubUnitListBean> companyDetails) {
            this.context = context;
            this.companyDetails = companyDetails;
            inflter = (LayoutInflater.from(context));
        }

        @Override
        public int getCount() {
            return companyDetails.size();
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
            names.setText(companyDetails.get(position).getSubUnitNameX());

            return convertView;
        }
    }


    public class EmployeeAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflter;
        List<ApiResponse.PersonsListBean> employeeDetailsList;

        public EmployeeAdapter(Context context, List<ApiResponse.PersonsListBean> employeeDetailsList) {
            this.context = context;
            this.employeeDetailsList = employeeDetailsList;
            inflter = (LayoutInflater.from(context));
        }

        @Override
        public int getCount() {
            return employeeDetailsList.size();
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

            if (employeeDetailsList != null && employeeDetailsList.isEmpty()){
                names.setText("No Person Found");
            }else{
                names.setText(employeeDetailsList.get(position).getPersonName());
            }

            return convertView;
        }
    }

}
