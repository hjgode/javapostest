package javapostest;

public class DirectIOBitmapInfo
{
  private int station;
  private String fileName;
  private int width;
  private int alignment;
  private int height;
  
  public DirectIOBitmapInfo(int station, String fileName, int width, int alignment)
  {
    this.station = station;
    this.fileName = new String(fileName);
    this.width = width;
    this.alignment = alignment;
  }
  
  public DirectIOBitmapInfo(int station, String fileName, int width, int alignment, int height)
  {
    this.station = station;
    this.fileName = new String(fileName);
    this.width = width;
    this.alignment = alignment;
    this.height = height;
  }
  
  public int getStation()
  {
    return this.station;
  }
  
  public String getFileName()
  {
    return this.fileName;
  }
  
  public int getWidth()
  {
    return this.width;
  }
  
  public int getAlignment()
  {
    return this.alignment;
  }
  
  public int getHeight()
  {
    return this.height;
  }
}