package com.example.administrator.goldaappnew.bean;



/**
 * Marker 地图点击弹窗实体类
 */
public class MarkerInfo {
    private int id;
    private String address;
    private String company;
    private String intro;
    private String mediatype;
    private String dateline;
    private String problem;
    private String lat;
    private String lng;
    private AdGreenBean ad_green;
    private AdRedBean ad_red;
    private String type;


    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public AdGreenBean getAd_green() {
		return ad_green;
	}

	public void setAd_green(AdGreenBean ad_green) {
		this.ad_green = ad_green;
	}

	public AdRedBean getAd_red() {
		return ad_red;
	}

	public void setAd_red(AdRedBean ad_red) {
		this.ad_red = ad_red;
	}

	public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getMediatype() {
        return mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}
