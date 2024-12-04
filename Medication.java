public class Medication {
  private String type;
  private String name;
  private double costPerGram;
  private double quantityGrams;
  private String description;
  private boolean restrictionStatus;
  private int lowStockThreshold; // Default value is 225 grams, currently cannot be changed

  public Medication(String medType, String medName, double medCost, double medQuantityGrams, String medDesc, boolean restriction) {
    type = medType;
    name = medName;
    costPerGram = medCost;
    quantityGrams = medQuantityGrams;
    description = medDesc;
    restrictionStatus = restriction;
    lowStockThreshold = 225;
  }

  public Medication() {
    type = "N/A";
    name = "N/A";
    costPerGram = 0.0;
    quantityGrams = 0.0;
    description = "N/A";
    restrictionStatus = false;
    lowStockThreshold = 225;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setCostPerGram(double cost) {
    this.costPerGram = cost;
  }

  public double getCostPerGram() {
    return costPerGram;
  }

  public void setQuantityGrams(double quantityGrams) {
    this.quantityGrams = quantityGrams;
  }

  public double getQuantityGrams() {
    return quantityGrams;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setRestrictionStatus(boolean status) {
    this.restrictionStatus = status;
  }

  public boolean getRestrictionStatus() {
    return restrictionStatus;
  }

  public void setLowStockThreshold(int threshold) {
    this.lowStockThreshold = threshold;
  }

  public int getLowStockThreshold() {
    return lowStockThreshold;
  }

  public boolean checkLowStock() {
    return quantityGrams < lowStockThreshold;
  }

  @Override
  public String toString() {
    return String.format("Name: %s, Type: %s, Quantity: %.2f grams, Low Stock Threshold: %d grams", name, type, quantityGrams, lowStockThreshold);
  }

//Converts medication to one line of CSV format
  public String toCSV() {
    return String.format("%s,%s,%.2f,%.2f,%s,%b", type, name, costPerGram, quantityGrams, description, restrictionStatus);
  }
}
