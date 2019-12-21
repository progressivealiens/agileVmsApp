package com.agile.agilevisitor.webapi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {

    private String status;
    private String msg;
    private String name;
    private String unitCode;
    private String unitName;
    private String unitAddress;
    private String subUnitName;
    private String landmark;
    private boolean profileAlreadyExists;
    private boolean wtmSection;
    private int visitorId;
    private String email;
    private String category;
    private String photo;
    private String adhharCard;
    private String companyName;
    private String mobile;
    private String logo;
    private String invites;
    private String visitor;
    private int passcode;
    private String qrLink;
    private String address;
    private int invitationId;
    private int subUnitId;
    private int meetToId;

    private List<SubUnitListBean> subUnitList;
    private List<PersonsListBean> personsList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitAddress() {
        return unitAddress;
    }

    public void setUnitAddress(String unitAddress) {
        this.unitAddress = unitAddress;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public boolean isProfileAlreadyExists() {
        return profileAlreadyExists;
    }

    public void setProfileAlreadyExists(boolean profileAlreadyExists) {
        this.profileAlreadyExists = profileAlreadyExists;
    }

    public String getSubUnitName() {
        return subUnitName;
    }

    public void setSubUnitName(String subUnitName) {
        this.subUnitName = subUnitName;
    }

    public boolean isWtmSection() {
        return wtmSection;
    }

    public void setWtmSection(boolean wtmSection) {
        this.wtmSection = wtmSection;
    }

    public int getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(int visitorId) {
        this.visitorId = visitorId;
    }


    public List<PersonsListBean> getPersonsList() {
        return personsList;
    }

    public void setPersonsList(List<PersonsListBean> personsList) {
        this.personsList = personsList;
    }

    public List<SubUnitListBean> getSubUnitList() {
        return subUnitList;
    }

    public void setSubUnitList(List<SubUnitListBean> subUnitList) {
        this.subUnitList = subUnitList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAdhharCard() {
        return adhharCard;
    }

    public void setAdhharCard(String adhharCard) {
        this.adhharCard = adhharCard;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getInvites() {
        return invites;
    }

    public void setInvites(String invites) {
        this.invites = invites;
    }

    public String getVisitor() {
        return visitor;
    }

    public void setVisitor(String visitor) {
        this.visitor = visitor;
    }

    public int getPasscode() {
        return passcode;
    }

    public void setPasscode(int passcode) {
        this.passcode = passcode;
    }

    public String getQrLink() {
        return qrLink;
    }

    public void setQrLink(String qrLink) {
        this.qrLink = qrLink;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(int invitationId) {
        this.invitationId = invitationId;
    }

    public int getSubUnitId() {
        return subUnitId;
    }

    public void setSubUnitId(int subUnitId) {
        this.subUnitId = subUnitId;
    }

    public int getMeetToId() {
        return meetToId;
    }

    public void setMeetToId(int meetToId) {
        this.meetToId = meetToId;
    }


    public static class SubUnitListBean {

        @SerializedName("subUnitName")
        private String subUnitNameX;
        private int subUnitId;
        private String subUnitCategory;

        public String getSubUnitNameX() {
            return subUnitNameX;
        }

        public void setSubUnitNameX(String subUnitNameX) {
            this.subUnitNameX = subUnitNameX;
        }

        public int getSubUnitId() {
            return subUnitId;
        }

        public void setSubUnitId(int subUnitId) {
            this.subUnitId = subUnitId;
        }

        public String getSubUnitCategory() {
            return subUnitCategory;
        }

        public void setSubUnitCategory(String subUnitCategory) {
            this.subUnitCategory = subUnitCategory;
        }
    }

    public static class PersonsListBean {

        private String personName;
        private int personId;

        public String getPersonName() {
            return personName;
        }

        public void setPersonName(String personName) {
            this.personName = personName;
        }

        public int getPersonId() {
            return personId;
        }

        public void setPersonId(int personId) {
            this.personId = personId;
        }
    }

}
