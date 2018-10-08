package com.example.administrator.goldaappnew.bean;

import java.io.Serializable;

//import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

public class AdRedBean implements Serializable{

	// { 'lid': '156', 'uid': '24', 'xid': '201292794011', 'board_id': '0',
	// 'dateline': '1348710512', 'belongto': '', 'province': '江苏省', 'city':
	// '南京市',
	// 'area': '建邺区', 'address': '江苏省南京市建邺区虎踞南路25号(所在商圈:南苑)',
	// 'company': '南京紫金广告有限公司', 'shortname': '紫金', 'intro': '国缘', 'bd_sn': '1',
	// 'gis_x': '118.7745590209961', 'gis_y': '32.044132232666016',
	// 'gis_pic':
	// '/data/attachment/gis/pic/201292794011_20129279426.jpg|/data/attachment/gis/pic/',
	// 'question': '', 'solove': '', 'result': '', 'remark': '', 'people_find':
	// '徐林伟', 'people_solove': '',
	// 'level': '无问题', 'icon': 'big_bridge', 'copy_status': '0' },
	private String lid;
	private String uid;
	private String xid;
	private String board_id;
	private String dateline;
	private String belongto;
	private String province;
	private String city;
	private String area;
	private String address;
	private String company;
	private String shortname;
	private String intro;
	private String bd_sn;
	private String gis_x;
	private String gis_y;
	private String gis_pic;
	private String question;
	private String solove;
	private String result;
	private String remark;
	private String people_find;
	private String people_solove;
	private String level;
	private String icon;
	private String copy_status;
	private String abcd;
	private String process_contact;
	private String process_tel;


	private String icon_type;
	private String icon_class;
	private String icon_cnname;


	@Override
	public String toString() {
		return "AdRedBean{" +
				"lid='" + lid + '\'' +
				", uid='" + uid + '\'' +
				", xid='" + xid + '\'' +
				", board_id='" + board_id + '\'' +
				", dateline='" + dateline + '\'' +
				", belongto='" + belongto + '\'' +
				", province='" + province + '\'' +
				", city='" + city + '\'' +
				", area='" + area + '\'' +
				", address='" + address + '\'' +
				", company='" + company + '\'' +
				", shortname='" + shortname + '\'' +
				", intro='" + intro + '\'' +
				", bd_sn='" + bd_sn + '\'' +
				", gis_x='" + gis_x + '\'' +
				", gis_y='" + gis_y + '\'' +
				", gis_pic='" + gis_pic + '\'' +
				", question='" + question + '\'' +
				", solove='" + solove + '\'' +
				", result='" + result + '\'' +
				", remark='" + remark + '\'' +
				", people_find='" + people_find + '\'' +
				", people_solove='" + people_solove + '\'' +
				", level='" + level + '\'' +
				", icon='" + icon + '\'' +
				", copy_status='" + copy_status + '\'' +
				", abcd='" + abcd + '\'' +
				", process_contact='" + process_contact + '\'' +
				", process_tel='" + process_tel + '\'' +
				", icon_type='" + icon_type + '\'' +
				", icon_class='" + icon_class + '\'' +
				", icon_cnname='" + icon_cnname + '\'' +
				'}';
	}

	public String getLid() {
		return lid;
	}

	public void setLid(String lid) {
		this.lid = lid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getXid() {
		return xid;
	}

	public void setXid(String xid) {
		this.xid = xid;
	}

	public String getBoard_id() {
		return board_id;
	}

	public void setBoard_id(String board_id) {
		this.board_id = board_id;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public String getBelongto() {
		return belongto;
	}

	public void setBelongto(String belongto) {
		this.belongto = belongto;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getBd_sn() {
		return bd_sn;
	}

	public void setBd_sn(String bd_sn) {
		this.bd_sn = bd_sn;
	}

	public String getGis_x() {
		return gis_x;
	}

	public void setGis_x(String gis_x) {
		this.gis_x = gis_x;
	}

	public String getGis_y() {
		return gis_y;
	}

	public void setGis_y(String gis_y) {
		this.gis_y = gis_y;
	}

	public String getGis_pic() {
		return gis_pic;
	}

	public void setGis_pic(String gis_pic) {
		this.gis_pic = gis_pic;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getSolove() {
		return solove;
	}

	public void setSolove(String solove) {
		this.solove = solove;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPeople_find() {
		return people_find;
	}

	public void setPeople_find(String people_find) {
		this.people_find = people_find;
	}

	public String getPeople_solove() {
		return people_solove;
	}

	public void setPeople_solove(String people_solove) {
		this.people_solove = people_solove;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getCopy_status() {
		return copy_status;
	}

	public void setCopy_status(String copy_status) {
		this.copy_status = copy_status;
	}

	public String getAbcd() {
		return abcd;
	}

	public void setAbcd(String abcd) {
		this.abcd = abcd;
	}

	public String getProcess_contact() {
		return process_contact;
	}

	public void setProcess_contact(String process_contact) {
		this.process_contact = process_contact;
	}

	public String getProcess_tel() {
		return process_tel;
	}

	public void setProcess_tel(String process_tel) {
		this.process_tel = process_tel;
	}

	public String getIcon_type() {
		return icon_type;
	}

	public void setIcon_type(String icon_type) {
		this.icon_type = icon_type;
	}

	public String getIcon_class() {
		return icon_class;
	}

	public void setIcon_class(String icon_class) {
		this.icon_class = icon_class;
	}

	public String getIcon_cnname() {
		return icon_cnname;
	}

	public void setIcon_cnname(String icon_cnname) {
		this.icon_cnname = icon_cnname;
	}
}
