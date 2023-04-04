package twx.core.db;

public class ForeignKey {
  private String name;
  
  private String referenceDataShapeName;
  
  private String referenceFieldName;
  
  private String identifier;
  
  private OnDeleteEnum onDelete = OnDeleteEnum.NO_ACTION;
  
  private boolean deleteReference = false;
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getReferenceDataShapeName() {
    return this.referenceDataShapeName;
  }
  
  public void setReferenceDataShapeName(String referenceDataShapeName) {
    this.referenceDataShapeName = referenceDataShapeName;
  }
  
  public String getReferenceFieldName() {
    return this.referenceFieldName;
  }
  
  public void setReferenceFieldName(String referenceFieldName) {
    this.referenceFieldName = referenceFieldName;
  }
  
  public String getIdentifier() {
    return this.identifier;
  }
  
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }
  
  public OnDeleteEnum getOnDelete() {
    return this.onDelete;
  }
  
  public void setOnDelete(OnDeleteEnum onDelete) {
    this.onDelete = onDelete;
  }
  
  public boolean isDeleteReference() {
    return this.deleteReference;
  }
  
  public void setDeleteReference(boolean deleteReference) {
    this.deleteReference = deleteReference;
  }
}
