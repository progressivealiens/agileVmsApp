package com.agile.agilevisitor.views.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.views.activity.visiting_panel.AllDetailsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectCategoryFragment extends Fragment implements View.OnClickListener {

    View v;

    @BindView(R.id.lin_visitor)
    LinearLayout linVisitor;
    @BindView(R.id.lin_vendor)
    LinearLayout linVendor;
    @BindView(R.id.lin_employee)
    LinearLayout linEmployee;
    @BindView(R.id.lin_candidate)
    LinearLayout linCandidate;

    private final int PERMISSIONS_CAMERA_REQUEST_CODE = 666;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_select_category, container, false);
        ButterKnife.bind(this, v);

        initialize();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!startRequestPermission()) {
            startRequestPermission();
        }
    }

    private void initialize() {
        linVisitor.setOnClickListener(this);
        linVendor.setOnClickListener(this);
        linEmployee.setOnClickListener(this);
        linCandidate.setOnClickListener(this);

        if (AllDetailsActivity.isEditDetails || AllDetailsActivity.isInviteDetails){
            AllDetailsActivity.tvTitle.setText("Verify Category");
        }else{
            AllDetailsActivity.tvTitle.setText("Select Category");
        }

        if (AllDetailsActivity.isEditDetails || AllDetailsActivity.isInviteDetails){
            if (AllDetailsActivity.categoryChoosen.equalsIgnoreCase("visitor")){
                linVisitor.setBackground(getResources().getDrawable(R.drawable.rect_round_selected_corners));
            }else if (AllDetailsActivity.categoryChoosen.equalsIgnoreCase("vendor")){
                linVendor.setBackground(getResources().getDrawable(R.drawable.rect_round_selected_corners));
            }else if (AllDetailsActivity.categoryChoosen.equalsIgnoreCase("employee")){
                linEmployee.setBackground(getResources().getDrawable(R.drawable.rect_round_selected_corners));
            }else if (AllDetailsActivity.categoryChoosen.equalsIgnoreCase("candidate")){
                linCandidate.setBackground(getResources().getDrawable(R.drawable.rect_round_selected_corners));
            }
        }
    }

    private boolean startRequestPermission() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_CAMERA_REQUEST_CODE);

            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_CAMERA_REQUEST_CODE) {
            if (grantResults.length>0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getActivity(), "Sorry!!!, you can\\'t use this app without granting permission", Toast.LENGTH_LONG).show();
                    startRequestPermission();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.lin_visitor:
                AllDetailsActivity.categoryChoosen = "visitor";
                Utils.changeFragmentAllDetails(new BasicDetailsFragment(), "frag_basic_details", AllDetailsActivity.activity);
                break;

            case R.id.lin_vendor:
                AllDetailsActivity.categoryChoosen = "vendor";
                Utils.changeFragmentAllDetails(new BasicDetailsFragment(), "frag_basic_details", AllDetailsActivity.activity);
                break;

            case R.id.lin_employee:
                AllDetailsActivity.categoryChoosen = "employee";
                Utils.changeFragmentAllDetails(new BasicDetailsFragment(), "frag_basic_details", AllDetailsActivity.activity);
                break;

            case R.id.lin_candidate:
                AllDetailsActivity.categoryChoosen = "candidate";
                Utils.changeFragmentAllDetails(new BasicDetailsFragment(), "frag_basic_details", AllDetailsActivity.activity);
                break;
        }
    }
}
