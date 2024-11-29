package com.advantech.srm.persistence.model;
// Generated 2019/9/23 �U�� 02:38:15 by Hibernate Tools 4.3.5.Final

import jakarta.persistence.*;

import java.util.Date;

import static jakarta.persistence.GenerationType.IDENTITY;


/**
 * VendorMasterEntity generated by hbm2java
 */
@Entity
@Table(name = "VendorMaster", schema = "Management")
public class VendorMasterEntity implements java.io.Serializable {

    public final static String seqNoConstant = "seqNo";

    public final static String groupKeyConstant = "groupKey";

    public final static String vendorTypeConstant = "vendorType";

    public final static String partCategoryConstant = "partCategory";

    public final static String vendorCodeConstant = "vendorCode";

    public final static String manufacturerCodeConstant = "manufacturerCode";

    public final static String isForiegnVendorConstant = "isForiegnVendor";

    public final static String vendorLevelConstant = "vendorLevel";

    public final static String vendorNameConstant = "vendorName";

    public final static String vendorName2Constant = "vendorName2";

    public final static String vendorName3Constant = "vendorName3";

    public final static String vendorName4Constant = "vendorName4";

    public final static String searchTermConstant = "searchTerm";

    public final static String taxNumberConstant = "taxNumber";

    public final static String vatregNumberConstant = "vatregNumber";

    public final static String taxNumber2Constant = "taxNumber2";

    public final static String taxNumber3Constant = "taxNumber3";

    public final static String taxNumber4Constant = "taxNumber4";

    public final static String responsiblePersonConstant = "responsiblePerson";

    public final static String formDateConstant = "formDate";

    public final static String countryCodeConstant = "countryCode";

    public final static String regionConstant = "region";

    public final static String cityConstant = "city";

    public final static String postCodeConstant = "postCode";

    public final static String addressConstant = "address";

    public final static String telephoneConstant = "telephone";

    public final static String teleExtensionConstant = "teleExtension";

    public final static String faxConstant = "fax";

    public final static String websiteConstant = "website";

    public final static String emailConstant = "email";

    public final static String equityCapitalConstant = "equityCapital";

    public final static String totalEmployeeConstant = "totalEmployee";

    public final static String mainProductConstant = "mainProduct";

    public final static String mainEquipmentConstant = "mainEquipment";

    public final static String mainClient1Constant = "mainClient1";

    public final static String mainClient2Constant = "mainClient2";

    public final static String mainClient3Constant = "mainClient3";

    public final static String mainVendorConstant = "mainVendor";

    public final static String currencyConstant = "currency";

    public final static String avgRevenueConstant = "avgRevenue";

    public final static String sourcerUserIdConstant = "sourcerUserId";

    public final static String lifecycleConstant = "lifecycle";

//    public final static String blockStatusConstant = "blockStatus";

    public final static String createUserConstant = "createUser";

    public final static String createDtConstant = "createDt";

    public final static String updateUserConstant = "updateUser";

    public final static String updateDtConstant = "updateDt";

    public final static String vendorPurchasingOrgEntitiesConstant = "vendorPurchasingOrgEntities";

    public final static String vendorUserEntitiesConstant = "vendorUserEntities";

    private Long seqNo;
    private String groupKey;
    private Integer vendorType;
    private String partCategory;
    private String vendorCode;
    private String manufacturerCode;
    private Integer isForiegnVendor;
    private String vendorLevel;
    private String vendorName;
    private String vendorName2;
    private String vendorName3;
    private String vendorName4;
    private String searchTerm;
    private String taxNumber;
    private String vatregNumber;
    private String taxNumber2;
    private String taxNumber3;
    private String taxNumber4;
    private String responsiblePerson;
    private Date formDate;
    private String countryCode;
    private String region;
    private String city;
    private String postCode;
    private String address;
    private String teleAreaCode;
    private String telephone;
    private String teleExtension;
    private String faxAreaCode;
    private Integer isProcessingArea0;
    private Integer isProcessingArea1;
    private Integer isProcessingArea2;
    private Integer isProcessingArea3;
    private Integer isProcessingArea4;
    private Integer isProcessingArea5;

    private String fax;
    private String website;
    private String email;
    private Long equityCapital;
    private Integer totalEmployee;
    private String mainProduct;
    private String mainEquipment;
    private String mainClient1;
    private String mainClient2;
    private String mainClient3;
    private String mainVendor;
    private String currency;
    private Long avgRevenue;
    private String sourcerUserId;
    private Integer lifecycle;
    private String billAddress;
//    private Integer blockStatus;
    private String createUser;
    private Date createDt;
    private String updateUser;
    private Date updateDt;
    private String hashKey;
    private Integer isWaiveSurvey;

    public VendorMasterEntity() {}

    public VendorMasterEntity(String groupKey, Integer vendorType, String partCategory, String vendorCode, Integer isForiegnVendor, String vendorName, String createUser, Date createDt, String hashKey) {
        this.groupKey = groupKey;
        this.vendorType = vendorType;
        this.partCategory = partCategory;
        this.vendorCode = vendorCode;
        this.isForiegnVendor = isForiegnVendor;
        this.vendorName = vendorName;
        this.createUser = createUser;
        this.createDt = createDt;
        this.hashKey = hashKey;
    }

    public VendorMasterEntity(String groupKey, Integer vendorType, String partCategory, String vendorCode, String manufacturerCode, Integer isForiegnVendor, String vendorLevel, String vendorName,
                              String vendorName2, String vendorName3, String vendorName4, String searchTerm, String taxNumber, String vatregNumber, String taxNumber2, String taxNumber3, String taxNumber4,
                              String responsiblePerson, Date formDate, String countryCode, String region, String city, String postCode, String address, String teleAreaCode, String telephone, String teleExtension, String faxAreaCode, String fax,
                              String website, String email, Long equityCapital, Integer totalEmployee, String mainProduct, String mainEquipment, String mainClient1, String mainClient2, String mainClient3,
                              String mainVendor, String currency, Long avgRevenue, String sourcerUserId, Integer lifecycle, Integer blockStatus, String createUser, Date createDt, String updateUser, Date updateDt, String hashKey
           ) {
        this.groupKey = groupKey;
        this.vendorType = vendorType;
        this.partCategory = partCategory;
        this.vendorCode = vendorCode;
        this.manufacturerCode = manufacturerCode;
        this.isForiegnVendor = isForiegnVendor;
        this.vendorLevel = vendorLevel;
        this.vendorName = vendorName;
        this.vendorName2 = vendorName2;
        this.vendorName3 = vendorName3;
        this.vendorName4 = vendorName4;
        this.searchTerm = searchTerm;
        this.taxNumber = taxNumber;
        this.vatregNumber = vatregNumber;
        this.taxNumber2 = taxNumber2;
        this.taxNumber3 = taxNumber3;
        this.taxNumber4 = taxNumber4;
        this.responsiblePerson = responsiblePerson;
        this.formDate = formDate;
        this.countryCode = countryCode;
        this.region = region;
        this.city = city;
        this.postCode = postCode;
        this.teleAreaCode = teleAreaCode;
        this.telephone = telephone;
        this.teleExtension = teleExtension;
        this.faxAreaCode = faxAreaCode;
        this.fax = fax;
        this.website = website;
        this.email = email;
        this.equityCapital = equityCapital;
        this.totalEmployee = totalEmployee;
        this.mainProduct = mainProduct;
        this.mainEquipment = mainEquipment;
        this.mainClient1 = mainClient1;
        this.mainClient2 = mainClient2;
        this.mainClient3 = mainClient3;
        this.mainVendor = mainVendor;
        this.currency = currency;
        this.avgRevenue = avgRevenue;
        this.sourcerUserId = sourcerUserId;
        this.lifecycle = lifecycle;
//        this.blockStatus = blockStatus;
        this.createUser = createUser;
        this.createDt = createDt;
        this.updateUser = updateUser;
        this.updateDt = updateDt;
        this.hashKey = hashKey;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)

    @Column(name = "SeqNo")
    public Long getSeqNo() {
        return this.seqNo;
    }

    public void setSeqNo(Long seqNo) {
        this.seqNo = seqNo;
    }

    @Column(name = "GroupKey", nullable = false)
    public String getGroupKey() {
        return this.groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    @Column(name = "VendorType", nullable = false)
    public Integer getVendorType() {
        return this.vendorType;
    }

    public void setVendorType(Integer vendorType) {
        this.vendorType = vendorType;
    }

    @Column(name = "PartCategory", nullable = false)
    public String getPartCategory() {
        return this.partCategory;
    }

    public void setPartCategory(String partCategory) {
        this.partCategory = partCategory;
    }

    @Column(name = "VendorCode", nullable = false)
    public String getVendorCode() {
        return this.vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    @Column(name = "ManufacturerCode")
    public String getManufacturerCode() {
        return this.manufacturerCode;
    }

    public void setManufacturerCode(String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    @Column(name = "IsForiegnVendor", nullable = false)
    public Integer getIsForiegnVendor() {
        return this.isForiegnVendor;
    }

    public void setIsForiegnVendor(Integer isForiegnVendor) {
        this.isForiegnVendor = isForiegnVendor;
    }

    @Column(name = "VendorLevel")
    public String getVendorLevel() {
        return this.vendorLevel;
    }

    public void setVendorLevel(String vendorLevel) {
        this.vendorLevel = vendorLevel;
    }

    @Column(name = "VendorName", nullable = false)
    public String getVendorName() {
        return this.vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    @Column(name = "VendorName2")
    public String getVendorName2() {
        return this.vendorName2;
    }

    public void setVendorName2(String vendorName2) {
        this.vendorName2 = vendorName2;
    }

    @Column(name = "VendorName3")
    public String getVendorName3() {
        return this.vendorName3;
    }

    public void setVendorName3(String vendorName3) {
        this.vendorName3 = vendorName3;
    }

    @Column(name = "VendorName4")
    public String getVendorName4() {
        return this.vendorName4;
    }

    public void setVendorName4(String vendorName4) {
        this.vendorName4 = vendorName4;
    }

    @Column(name = "SearchTerm")
    public String getSearchTerm() {
        return this.searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @Column(name = "TaxNumber")
    public String getTaxNumber() {
        return this.taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    @Column(name = "VATRegNumber")
    public String getVatregNumber() {
        return this.vatregNumber;
    }

    public void setVatregNumber(String vatregNumber) {
        this.vatregNumber = vatregNumber;
    }

    @Column(name = "TaxNumber2")
    public String getTaxNumber2() {
        return this.taxNumber2;
    }

    public void setTaxNumber2(String taxNumber2) {
        this.taxNumber2 = taxNumber2;
    }

    @Column(name = "TaxNumber3")
    public String getTaxNumber3() {
        return this.taxNumber3;
    }

    public void setTaxNumber3(String taxNumber3) {
        this.taxNumber3 = taxNumber3;
    }

    @Column(name = "TaxNumber4")
    public String getTaxNumber4() {
        return this.taxNumber4;
    }

    public void setTaxNumber4(String taxNumber4) {
        this.taxNumber4 = taxNumber4;
    }

    @Column(name = "ResponsiblePerson")
    public String getResponsiblePerson() {
        return this.responsiblePerson;
    }

    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "FormDate", length = 10)
    public Date getFormDate() {
        return this.formDate;
    }

    public void setFormDate(Date formDate) {
        this.formDate = formDate;
    }

    @Column(name = "CountryCode")
    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Column(name = "Region")
    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Column(name = "City")
    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "PostCode")
    public String getPostCode() {
        return this.postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Column(name = "Address")
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "TeleAreaCode")
    public String getTeleAreaCode() {
        return this.teleAreaCode;
    }

    public void setTeleAreaCode(String teleAreaCode) {
        this.teleAreaCode = teleAreaCode;
    }

    @Column(name = "Telephone")
    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name = "TeleExtension")
    public String getTeleExtension() {
        return this.teleExtension;
    }

    public void setTeleExtension(String teleExtension) {
        this.teleExtension = teleExtension;
    }

    @Column(name = "FaxAreaCode")
    public String getFaxAreaCode() {
        return this.faxAreaCode;
    }

    public void setFaxAreaCode(String faxAreaCode) {
        this.faxAreaCode = faxAreaCode;
    }

    @Column(name = "Fax")
    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Column(name = "Website")
    public String getWebsite() {
        return this.website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Column(name = "Email")
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "EquityCapital")
    public Long getEquityCapital() {
        return this.equityCapital;
    }

    public void setEquityCapital(Long equityCapital) {
        this.equityCapital = equityCapital;
    }

    @Column(name = "TotalEmployee")
    public Integer getTotalEmployee() {
        return this.totalEmployee;
    }

    public void setTotalEmployee(Integer totalEmployee) {
        this.totalEmployee = totalEmployee;
    }

    @Column(name = "MainProduct")
    public String getMainProduct() {
        return this.mainProduct;
    }

    public void setMainProduct(String mainProduct) {
        this.mainProduct = mainProduct;
    }

    @Column(name = "MainEquipment")
    public String getMainEquipment() {
        return this.mainEquipment;
    }

    public void setMainEquipment(String mainEquipment) {
        this.mainEquipment = mainEquipment;
    }

    @Column(name = "MainClient1")
    public String getMainClient1() {
        return this.mainClient1;
    }

    public void setMainClient1(String mainClient1) {
        this.mainClient1 = mainClient1;
    }

    @Column(name = "MainClient2")
    public String getMainClient2() {
        return this.mainClient2;
    }

    public void setMainClient2(String mainClient2) {
        this.mainClient2 = mainClient2;
    }

    @Column(name = "MainClient3")
    public String getMainClient3() {
        return this.mainClient3;
    }

    public void setMainClient3(String mainClient3) {
        this.mainClient3 = mainClient3;
    }

    @Column(name = "MainVendor")
    public String getMainVendor() {
        return this.mainVendor;
    }

    public void setMainVendor(String mainVendor) {
        this.mainVendor = mainVendor;
    }

    @Column(name = "Currency")
    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name = "AvgRevenue")
    public Long getAvgRevenue() {
        return this.avgRevenue;
    }

    public void setAvgRevenue(Long avgRevenue) {
        this.avgRevenue = avgRevenue;
    }

    @Column(name = "SourcerUserId")
    public String getSourcerUserId() {
        return this.sourcerUserId;
    }

    public void setSourcerUserId(String sourcerUserId) {
        this.sourcerUserId = sourcerUserId;
    }

    @Column(name = "Lifecycle")
    public Integer getLifecycle() {
        return this.lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

//    @Column(name = "BlockStatus")
//    public Integer getBlockStatus() {
//        return this.blockStatus;
//    }
//
//    public void setBlockStatus(Integer blockStatus) {
//        this.blockStatus = blockStatus;
//    }

    @Column(name = "CreateUser", nullable = false)
    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CreateDt", nullable = false, length = 23)
    public Date getCreateDt() {
        return this.createDt;
    }

    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }

    @Column(name = "UpdateUser")
    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UpdateDt", length = 23)
    public Date getUpdateDt() {
        return this.updateDt;
    }

    public void setUpdateDt(Date updateDt) {
        this.updateDt = updateDt;
    }

    @Column(name = "HashKey")
    public String getHashKey() {
        return this.hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    @Column(name = "IsProcessingArea0")
    public Integer getIsProcessingArea0() {
        return isProcessingArea0;
    }

    public void setIsProcessingArea0(Integer isProcessingArea0) {
        this.isProcessingArea0 = isProcessingArea0;
    }
    @Column(name = "IsProcessingArea1")
    public Integer getIsProcessingArea1() {
        return isProcessingArea1;
    }

    public void setIsProcessingArea1(Integer isProcessingArea1) {
        this.isProcessingArea1 = isProcessingArea1;
    }
    @Column(name = "IsProcessingArea2")
    public Integer getIsProcessingArea2() {
        return isProcessingArea2;
    }

    public void setIsProcessingArea2(Integer isProcessingArea2) {
        this.isProcessingArea2 = isProcessingArea2;
    }
    
    @Column(name = "IsProcessingArea3")
    public Integer getIsProcessingArea3() {
        return isProcessingArea3;
    }

    public void setIsProcessingArea3(Integer isProcessingArea3) {
        this.isProcessingArea3 = isProcessingArea3;
    }
    
    @Column(name = "IsProcessingArea4")
    public Integer getIsProcessingArea4() {
        return isProcessingArea4;
    }

    public void setIsProcessingArea4(Integer isProcessingArea4) {
        this.isProcessingArea4 = isProcessingArea4;
    }
    
    @Column(name = "IsProcessingArea5")
    public Integer getIsProcessingArea5() {
        return isProcessingArea5;
    }

    public void setIsProcessingArea5(Integer isProcessingArea5) {
        this.isProcessingArea5 = isProcessingArea5;
    }
    
    @Column(name = "BillAddress")
	public String getBillAddress() {
		return billAddress;
	}
	public void setBillAddress(String billAddress) {
		this.billAddress = billAddress;
	}
	
	@Column(name = "IsWaiveSurvey", nullable = false)
    public Integer getIsWaiveSurvey() {
        return this.isWaiveSurvey;
    }

    public void setIsWaiveSurvey(Integer isWaiveSurvey) {
        this.isWaiveSurvey = isWaiveSurvey;
    }
}
