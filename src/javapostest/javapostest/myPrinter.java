package javapostest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.ibm.poskbd.event.StatusEvent;
import com.ibm.poskbd.event.StatusListener;

import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.events.DirectIOEvent;
import jpos.events.DirectIOListener;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.OutputCompleteEvent;
import jpos.events.OutputCompleteListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;

public class myPrinter implements ErrorListener, OutputCompleteListener, StatusUpdateListener, DirectIOListener {
	static POSPrinter _posPrinter=null;
	private static final int receipt = POSPrinterConst.PTR_S_RECEIPT;
	private static final int[] SET_UPOS_MODE = { 0 };
	private static final int[] SET_DIRECTIO_MODE = { 1 };
	
	private int directIOEventState = 1;
	private boolean waitStatusAfterReset = false;
	private static final byte[] PRINTER_SATUS = { 27, 118 };
	
	public static POSPrinter getInstance(){
		if (_posPrinter == null) {
			sysprint("new...");
			_posPrinter=new POSPrinter();
		}
		return _posPrinter;
	}
	
	boolean _isOpen=false;
	public boolean getIsOpen() {
		return _isOpen;
	}
	
	boolean _isClaimed=false;
	public boolean getIsClaimed() {
		return _isClaimed;
	}
	
	boolean _isEnabled=false;
	public boolean getIsEnabled() {
		return _isEnabled;
	}
	
	public myPrinter(String sPrinter) throws Exception{
		_posPrinter=getInstance();
		try {
			sysprint("open..." + sPrinter);
			_posPrinter.open(sPrinter);
			sysprint("asyncMode = false...");
			_posPrinter.setAsyncMode(false);
			_isOpen=true;
		}catch (Exception ex) {			
			throw ex;
		}
	}
	
	public void claim () throws Exception{
		_posPrinter=getInstance();
		try {
			sysprint("add Listeners...");
			_posPrinter.addStatusUpdateListener(this);
			_posPrinter.addDirectIOListener(this);
			_posPrinter.addErrorListener(this);
			_posPrinter.addOutputCompleteListener(this);
			sysprint("claim...");
			_posPrinter.claim(1000);
			_isClaimed=true;
		}catch(Exception ex)
		{
			throw ex;
		}
	}

	public void release () throws Exception{
		_posPrinter=getInstance();
		try {
			sysprint("release...Listeners");
			_posPrinter.removeStatusUpdateListener(this);
			_posPrinter.removeDirectIOListener(this);
			_posPrinter.removeErrorListener(this);
			_posPrinter.removeOutputCompleteListener(this);
			sysprint("release...");
			_posPrinter.release();
			_isClaimed=true;
		}catch(Exception ex)
		{
			throw ex;
		}
	}
	
	public void enable() throws Exception{
		_posPrinter=getInstance();
		try {
		sysprint("setDeviceEnabled...true");
		_posPrinter.setDeviceEnabled(true);
		_isEnabled=true;
		}catch(Exception ex)
		{
			throw ex;
		}
		
	}
	
	public void close() throws Exception{
		_posPrinter=getInstance();
		try {
		sysprint("close...");
		_posPrinter.close();
		_isOpen=false;
		}catch(Exception ex)
		{
			throw ex;
		}
		
	}
	
	public void disable() throws Exception{
		_posPrinter=getInstance();
		try {
		sysprint("setDeviceEnabled...false");
		_posPrinter.setDeviceEnabled(false);
		_isEnabled=false;
		}catch(Exception ex)
		{
			throw ex;
		}
		
	}

	public String getHealth() throws Exception{
		POSPrinter ptr=getInstance();
		String s="";
		try {
			s = ptr.getCheckHealthText();
			sysprint("START Printer CAPs:" + "\n =============================================================");
			sysprint("getCheckHealthText: " +s);
		}
		catch(Exception ex)
		{
			throw ex;
		}
		return s;
	}
	
	public void printCAPs() throws Exception{
		POSPrinter ptr=getInstance();
		sysprint("START Printer CAPs:" + "\n =============================================================");
		try {
	        // Common Cap/Properties
	        sysprint("DeviceControlVersion = "
	                + ptr.getDeviceControlVersion());
	        sysprint("DeviceControlDescription = "
	                + ptr.getDeviceControlDescription());
	        sysprint("DeviceServiceVersion = "
	                + ptr.getDeviceServiceVersion());
	        sysprint("DeviceServiceDescription = "
	                + ptr.getDeviceServiceDescription());
	        sysprint("CapCompareFirmwareVersion = " + 
	                ptr.getCapCompareFirmwareVersion());
	        String capPowerReportingName = "";
	        switch(ptr.getCapPowerReporting())
	        {
	            case JposConst.JPOS_PR_NONE:
	                capPowerReportingName += "JPOS_PR_NONE = ";
	                break;
	            case JposConst.JPOS_PR_STANDARD:
	                capPowerReportingName += "JPOS_PR_STANDARD = ";
	                break;
	            case JposConst.JPOS_PR_ADVANCED:
	                capPowerReportingName += "JPOS_PR_ADVANCED = ";
	                break;
	        }
	        sysprint("CapPowerReporting = "+ capPowerReportingName + 
	                ptr.getCapPowerReporting());
	        sysprint("CapStatisticsReporting = " + 
	                ptr.getCapStatisticsReporting());
	        sysprint("CapUpdateFirmware = " +ptr.getCapUpdateFirmware());
	        sysprint("CapUpdateStatistics = " + 
	                ptr.getCapUpdateStatistics());
	        // Specific Cap/Properties
	        sysprint("CapCapRecPresent = " +ptr.getCapRecPresent());
	        sysprint("CapCapRecBarcode = " +ptr.getCapRecBarCode());
	        sysprint("CapCapRecBitmap = " +ptr.getCapRecBitmap());
	        sysprint("CapCapSlpPresent = " +ptr.getCapSlpPresent());
	        sysprint("CapCapSlpBarcode = " +ptr.getCapSlpBarCode());
	        sysprint("CapCapSlpBitmap = " +ptr.getCapSlpBitmap());	
			}catch(JposException ex) {
				throw (ex);
			}
		sysprint("END Printer CAPs" + "\n =============================================================");

	}
	
	public void printReceipt() throws Exception{
		if (! _isEnabled) {
			sysprint("printer not enabled");
			return;
		}
		try {
			POSPrinter ptr=getInstance();
	        ptr.setAsyncMode(false);
	        ptr.setRecLineChars(32);
	        int width = ptr.getRecLineWidth()/2;
	        ptr.setBitmap(1,receipt,
	                "javapostest/res/images/ToshibaLogo.gif",
	                width,
	                POSPrinterConst.PTR_BM_CENTER);
	        ptr.transactionPrint(receipt,POSPrinterConst.PTR_TP_TRANSACTION);
	        ptr.printNormal(receipt,"\u001b|1B");
	        ptr.printNormal(receipt,"\u001b|N\u001b|bC\u001b|3C\u001b|cATICKET STORE\n");
	        ptr.printNormal(receipt,"\u001b|N\u001b|cAMMM/DD/YY  HH:MM\n\n");
	        ptr.printNormal(receipt,"\u001b|bCStore Number:\u001b|N 8888\n");
	        ptr.printNormal(receipt,"\u001b|bCSeller:\u001b|N Your Name\n\n");
	        ptr.printNormal(receipt,"\u001b|N\u001b|uCDescription\u001b|cANo.Items\u001b|rASub-Total\n");
	        ptr.printNormal(receipt,"\u001b|NComputer\u001b|cA1\u001b|bC\u001b|rA433.90\n");
	        ptr.printNormal(receipt,"\u001b|NLaptop\u001b|cA1\u001b|bC\u001b|rA689.70\n");
	        ptr.printNormal(receipt,"\u001b|N\u001b|bC\u001b|2vCTOTAL AMOUNT\u001b|rA1123.60\n\n");
	        ptr.printNormal(receipt,"\u001b|NCreditCard (####-####-####-####)\n");
	        ptr.printNormal(receipt,"\u001b|N\u001b|bC\u001b|2vCAmount of Charge\u001b|rA1123.60\n\n");
	        ptr.printNormal(receipt,"\u001b|cA(  # sold items:   2 )\n");        
	        ptr.printNormal(receipt,"\u001b|cATAXES INCLUDED\n");
	        if(ptr.getCapRecBarCode()){
	        	ptr.printBarCode(receipt,
	        			"75010173943",
	        			POSPrinterConst.PTR_BCS_UPCA,
	        			100,
	        			(int)(ptr.getRecLineWidth()*.75),
	        			POSPrinterConst.PTR_BC_CENTER,
	        			POSPrinterConst.PTR_BC_TEXT_BELOW);
	        }
	        ptr.printNormal(receipt,"\u001b|bC\u001b|cAThis ticket is needed for\n");
	        ptr.printNormal(receipt,"\u001b|bC\u001b|cAany return or exchange\n\n");
	        ptr.printNormal(receipt,"\u001b|cA THANK YOU FOR YOUR BUSINESS\n\n");
	        ptr.printBitmap(receipt,
	                "javapostest/res/images/ToshibaLogo.gif",
	                POSPrinterConst.PTR_BM_ASIS,
	                POSPrinterConst.PTR_BM_CENTER);
	        ptr.printNormal(receipt,"\u001b|95fP");
	        ptr.transactionPrint(receipt,POSPrinterConst.PTR_TP_NORMAL);
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	public void setFactoryDefaults() throws Exception{
		POSPrinter ptr=getInstance();
		try {
		byte[] arrayOfByte = { 1, 70, 65, 67 };
		sysprint("setDirectIOMode...");
		setDirectIOMode(ptr);// getPrinter());
		sysprint("setDirectIOCommand...");
		sendDirectIOCommand(ptr /* getPrinter() */, arrayOfByte);
		sysprint("setUPOSMode...");
		setUPOSMode(ptr);// getPrinter());
		_isClaimed=false;
		_isEnabled=false;
		}catch(Exception ex) {
			throw ex;
		}
	}
	
	public static void setUPOSMode(POSPrinter paramPOSPrinter)
	{
		int[] arrayOfInt = { 0, 0, 0, 0 };
		try
		{
			paramPOSPrinter.directIO(1000, arrayOfInt, null);
			sysprint("setUPOSMode..." + toFormatedHexString(arrayOfInt));
			if (arrayOfInt[0] == 1)
			{
				delay(200L);
				sysprint("setDeviceEnabled(false)...");
				paramPOSPrinter.setDeviceEnabled(false);
				sysprint("directIO SET_UPOS_MODE...");
				paramPOSPrinter.directIO(1001, SET_UPOS_MODE, null);
				sysprint("setDeviceEnabled(true)...");
				paramPOSPrinter.setDeviceEnabled(true);
			}
		}
		catch (JposException localJposException)
		{
			sysprint(localJposException.getMessage());
		}
	}
	public static void setDirectIOMode(POSPrinter paramPOSPrinter)
	{
		int[] arrayOfInt = { 0, 0, 0, 0 };
		try
		{
			paramPOSPrinter.directIO(1000, arrayOfInt, null);
			if (arrayOfInt[0] == 0)
			{
				delay(200L);
				paramPOSPrinter.setDeviceEnabled(false);
				paramPOSPrinter.directIO(1001, SET_DIRECTIO_MODE, null);
				paramPOSPrinter.setDeviceEnabled(true);
			}
		}
		catch (JposException localJposException)
		{
			sysprint(localJposException.getMessage());
		}
	}
	
	public static synchronized void sendDirectIOCommand(POSPrinter paramPOSPrinter, byte[] paramArrayOfByte)
	{
		sysprint("sendDirectIO : " + toFormatedHexString(paramArrayOfByte));
		try
		{
			sysprint("directIO 1002...");
			paramPOSPrinter.directIO(1002, null, paramArrayOfByte);
		}
		catch (JposException localJposException)
		{
			sysprint(localJposException.getMessage());

		}
	}

	public static String toFormatedHexString(byte[] bytes) {
		// convert byte[] to string
		String s = new String(bytes, StandardCharsets.UTF_8);
		return s;
	}
	public static String toFormatedHexString(int[] bytes) {
		// convert byte[] to string
		String s = Arrays.toString(bytes);
		return s;
	}
	
	public static void delay(long ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch(InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}
	
	@Override
	public void statusUpdateOccurred(StatusUpdateEvent arg0) {
		// TODO Auto-generated method stub
    	boolean printStatus = false;
        int status = arg0.getStatus();

    	//Note: not all the status are going to be printed
    	// in the text area, just the most relevant
        String statusMsg = "StatusUpdateOccurred - ";
        switch (status)
        {
            case JposConst.JPOS_SUE_POWER_ONLINE:
                statusMsg += "JPOS_SUE_POWER_ONLINE";
                printStatus = true;
                break;

            case JposConst.JPOS_SUE_POWER_OFF_OFFLINE:
            case JposConst.JPOS_SUE_POWER_OFFLINE:
                statusMsg += "JPOS_SUE_POWER_OFF_OFFLINE";
                printStatus = true;
                break;
                
            case POSPrinterConst.PTR_SUE_IDLE:
                statusMsg += "PTR_SUE_IDLE";
                printStatus = true;
                break;
            case POSPrinterConst.PTR_SUE_REC_COVER_OK:
                statusMsg += "PTR_SUE_REC_COVER_OK";
                printStatus = true;
                break;
            case POSPrinterConst.PTR_SUE_REC_COVER_OPEN:
                statusMsg += "PTR_SUE_REC_COVER_OPEN";
                printStatus = true;
                break;
            case POSPrinterConst.PTR_SUE_SLP_COVER_OK:
                statusMsg += "PTR_SUE_SLP_COVER_OK";
                printStatus = true;
                break;
            case POSPrinterConst.PTR_SUE_SLP_COVER_OPEN:
                statusMsg += "PTR_SUE_SLP_COVER_OPEN";
                printStatus = true;
                break;
            case POSPrinterConst.PTR_SUE_SLP_EMPTY:
                statusMsg += "PTR_SUE_SLP_EMPTY";
                printStatus = true;
                break;
            case POSPrinterConst.PTR_SUE_SLP_PAPEROK:
                statusMsg += "PTR_SUE_SLP_PAPEROK";
                printStatus = true;
                break;
        }
        //display the status update received
        //System.out.println(statusMsg);
		sysprint("statusUpdateOccurred: " + statusMsg);
	}

	@Override
	public void directIOOccurred(DirectIOEvent arg0) {
		// TODO Auto-generated method stub
		sysprint("directIOOccurred: " + arg0.toString());
		
	}

	@Override
	public void errorOccurred(ErrorEvent arg0) {
		// TODO Auto-generated method stub
		sysprint("statusUpdateOccurred: " + arg0.toString());
		
	}

	@Override
	public void outputCompleteOccurred(OutputCompleteEvent arg0) {
		// TODO Auto-generated method stub
		sysprint("outputCompleteOccurred: " + arg0.toString());
		
	}
	
	public void printLogoByNo(int n) {
		if (! _isEnabled) {
			sysprint("printer not enabled");
			return;
		}
		try {
			POSPrinter ptr=getInstance();
	        ptr.setAsyncMode(false);
	        ptr.setRecLineChars(32);
	        int width = ptr.getRecLineWidth()/2;
	        ptr.transactionPrint(receipt,POSPrinterConst.PTR_TP_TRANSACTION);
	        ptr.printNormal(receipt,"\u001b|1B"); //LOGO 1 ?????
	        //ptr.printNormal(receipt,"\u001b|N\u001b|bC\u001b|3C\u001b|cATICKET STORE\n");
	        //ptr.printNormal(receipt, "X'1D2F;00;1'\n"); // 00 is density=normal, 1 is logo number
	        // or use GS | m logo#, "\u001D|001"
	        //ptr.printNormal(receipt,"\u001b|N\u001b|bC\u001b|3C\u001b|cATICKET STORE\n");
	        //ptr.printNormal(receipt, "\u001D|001"); // 00 is density=normal, 1 is logo number
	        //ptr.printNormal(receipt,"\u001b|95fP");
	        ptr.transactionPrint(receipt,POSPrinterConst.PTR_TP_NORMAL);
		}catch (Exception ex) {
			sysprint(ex.getMessage());
		}
	}
	void myprintLogo(int paramInt1, int paramInt2, int paramInt3) throws JposException {
		try {
			int[] arrayOfInt = { paramInt2 };
			int i = 120;
			DirectIOBitmapInfo localDirectIOBitmapInfo = new DirectIOBitmapInfo(paramInt1, "", i, paramInt3);
			//sendDirectIOCommand(2, arrayOfInt, localDirectIOBitmapInfo);
			getInstance().directIO(2, null, localDirectIOBitmapInfo);
		} catch (JposException ex) {
			sysprint(ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			sysprint(ex.getMessage());
			throw ex;
		}
	}

	public void printLogo(int idx)  {
		int i = idx == 0 ? 2 : 4;
		String align = "Center"; //"Left"
		int j = align.equals("Center") ? 1 : align.equals("Left") ? 0 : 2;
	    try
	    {
	      myprintLogo(i, idx + 1, j);
	    }
	    catch (JposException localJposException1)
	    {
	      sysprint(localJposException1.getMessage());
	      return;
	    }
	    try
	    {
	      if (i == 4)
	      {
	        getInstance().beginRemoval(5000);
	        getInstance().endRemoval();
	      }
	    }
	    catch (JposException localJposException2)
	    {
	      localJposException2.printStackTrace();
	    }
	}
	
	public static void sysprint(String s) {
		System.out.println(s);
	}
}
