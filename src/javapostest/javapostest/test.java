package javapostest;

import jpos.events.DirectIOEvent;
import jpos.events.DirectIOListener;
import jpos.events.ErrorEvent;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;
import jpos.util.JposPropertiesConst;

import java.io.Console;
import java.io.File;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import com.ibm.posj.Handle;
import com.ibm.posj.PosSystemManager;
import com.ibm.poskbd.event.StatusEvent;
import com.ibm.poskbd.event.StatusListener;

import jpos.*;


public class test implements jpos.events.ErrorListener, StatusListener, StatusUpdateListener, DirectIOListener{

	static jpos.POSPrinter prt;
	static myPrinter mPrinter=null;
	private static final int receipt = POSPrinterConst.PTR_S_RECEIPT;
	  
	private static final int[] SET_UPOS_MODE = { 0 };
	private static final int[] SET_DIRECTIO_MODE = { 1 };
	
	private int directIOEventState = 1;
	private boolean waitStatusAfterReset = false;
	private static final byte[] PRINTER_SATUS = { 27, 118 };
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			sysprint(dumpClassPath());
			sysprint("#############################"); //System.exit(-1);
			
			test myTest=new test();
//			System.setProperty("jpos.config.populatorFile", "jposxml.cfg");  
//			System.setProperty("jpos.util.tracing.TurnOnNamedTracers", "JposServiceLoader,SimpleEntryRegistry,SimpleRegPopulator,XercesRegPopulator");  
//			System.setProperty("jpos.util.tracing.TurnOnAllNamedTracers", "ON"); 
//			System.setProperty("jpos.loader.serviceManagerClass", "jpos.loader.simple.SimpleServiceManager");
//			System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, "/opt/tgcs/javapos/config/jpos.xml"); //System.getenv("jposxml_path"));
			
			//jpos.loader.simple.SimpleServiceManager loader= new jpos.loader.simple.SimpleServiceManager();
//			jpos.config.simple.xml.XercesRegPopulator loader = new jpos.config.simple.xml.XercesRegPopulator();
//			loader.load("/opt/tgcs/javapos/config/jpos.xml");
			
			//#################################
			
			/*
			myPrinter mPrinter = new myPrinter("POSPrinter1");
			mPrinter.claim();			
			mPrinter.enable();
			mPrinter.printCAPs();
			mPrinter.printDemo();
			mPrinter.setFactoryDefaults();
			System.exit(-2);
			*/
			
			//#################################
			
			mPrinter = null;
			java.util.Scanner sc = new java.util.Scanner(System.in);
			int i = -1; String inp = Integer.toString(i);
			while(true) {
					printMenu();
					System.out.print("Input: ");
	//				i = sc.nextInt();
	//				inp = Integer.toString(i);
					inp = sc.nextLine();
					if (inp.equals("1"))
						mPrinter=new myPrinter("POSPrinter1");
					else if (inp.equals("2"))
						mPrinter.claim();
					else if (inp.equals("3"))
						mPrinter.enable();
					else if (inp.equals("4"))
						mPrinter.printCAPs();
					else if (inp.equals("5"))
						if (mPrinter.getIsEnabled())
							mPrinter.printReceipt();
						else
							sysprint("Printer not enabled");
					else if (inp.equals("6"))
						mPrinter.setFactoryDefaults();
					else if (inp.equals("a"))
						mPrinter.printLogoByNo(i);
					else if (inp.equals("7"))
						mPrinter.disable();
					else if (inp.equals("8"))
						mPrinter.release();
					else if (inp.equals("9"))
						mPrinter.close();
					else if (inp.equals("R")) {
						mPrinter=new myPrinter("POSPrinter1");
						mPrinter.claim();
						mPrinter.enable();
						mPrinter.setFactoryDefaults();
						mPrinter.release();
						mPrinter.close();		
						}				
					else if (inp.equals("0"))
						System.exit(0);
			}
/*			
			sysprint("START: new POSPrinter()...");
			
			// Init printer
			POSPrinter prt=new POSPrinter();
			prt.addErrorListener(myTest);
			prt.addStatusUpdateListener(myTest);
			
			sysprint("open...");
			prt.open("POSPrinter1");
			sysprint("asyncMode = false...");
			prt.setAsyncMode(false);

			sysprint("claim...");
			prt.claim(1000);
			sysprint("setDeviceEnabled...true");
			prt.setDeviceEnabled(true);
			
			sysprint("START Printer CAPs:" + "\n =============================================================");
			myTest.printCaps(prt);
			sysprint("END Printer CAPs" + "\n =============================================================");
			
			sysprint("print ticketTest...");
			myTest.ticketTest(prt);
			
			sysprint("Defaults...");
			setFactoryDefaults(prt);
			sysprint("DONE Defaults...");
			
			sysprint("setDeviceEnabled...false");
			prt.setDeviceEnabled(false);
			sysprint("release...");
			prt.release();
			
			prt.removeErrorListener(myTest);
			prt.removeStatusUpdateListener(myTest);
			
			sysprint("close...");
			prt.close();
*/			
			
		}catch(JposException ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			
		}
		sysprint("END");
		System.exit(0);
	}
	
	public static void printMenu() {
		String o="null", c="            ", e="---------";
		if(mPrinter != null) {
			o=mPrinter.getIsOpen() ? "open":"close";
			c=mPrinter.getIsClaimed() ? "is claimed  ": 
				                        "not claimed ";
			e=mPrinter.getIsEnabled() ? "enabled  ":
				                        "disabled ";
		}
		String s = "+--------------------------------------+\n";
		s+=        "| [1] open printer                     |\n";
		s+=        "| [2] claim printer                    |\n";
		s+=        "| [3] enable printer                   |\n";
		s+=        "| [4] show DevCAPs                     |\n";
		s+=        "| [5] print Demo                       |\n";
		s+=        "| [a] print Logo                       |\n";
		s+=        "| [6] reset to Factory defaults        |\n";
		s+=        "| [7] disable printer                  |\n";
		s+=        "| [8] release printer                  |\n";
		s+=        "| [9] close printer                    |\n";
		s+=        "| [R] RESET printer to Factory in all  |\n";
		s+=        "| [0] exit                             |\n";
		s+=        "+--------------------------------------+\n";
		//          | open is claimed__ enabled__          |
		s+=        "| "+o+" "+c+" "+e+"" + "          |\n";
		s+=        "+--------------------------------------+\n";
		sysprint(s);
	}
	
	public static void sysprint(String s) {
		System.out.println(s);
	}
	@Override
	public void errorOccurred(ErrorEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println(arg0.toString());
	}
	 /**
     * implementation of statusUpdateListener interface it is called when the
     * status of the device is updated
     * 
     * @param e
     *            the event generated by the device
     */
    public void statusUpdateOccurred(StatusUpdateEvent e)
    {
    	boolean printStatus = false;
        int status = e.getStatus();

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
        	System.out.println(statusMsg);

    }
	@Override
	public void statusReceived(StatusEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println(arg0.toString());
	}
	
	public void printCaps(POSPrinter ptr) throws JposException {
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
	}
    public void ticketTest(POSPrinter ptr)throws JposException
    {
        
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
    	
    }

	@Override
	public void directIOOccurred(DirectIOEvent paramDirectIOEvent) {
		// TODO Auto-generated method stub
		sysprint(paramDirectIOEvent.toString());
	    sysprint("directIOEvent: " + toFormatedHexString((byte[])paramDirectIOEvent.getObject()));
	    switch (this.directIOEventState)
	    {
	    case 1: 
	      directIOEventOccurredNormalState(paramDirectIOEvent);
	      break;
	    case 2: 
	      directIOEventOccurredHeadHealthState(paramDirectIOEvent);
	      break;
	    case 3: 
	      directIOEventOccurredDeviceId(paramDirectIOEvent);
	      break;
	    case 4: 
	      directIOEventOccurredStatusAfterReset(paramDirectIOEvent);
	      break;
	    case 5: 
	      directIOEventOccurredKn4(paramDirectIOEvent);
	    }
	}
	
	public POSPrinter getPrinter() {
		return this.prt;
	}
	
	public static void setFactoryDefaults(POSPrinter ptr)
	{
		byte[] arrayOfByte = { 1, 70, 65, 67 };
		setDirectIOMode(ptr);// getPrinter());
		sendDirectIOCommand(ptr /* getPrinter() */, arrayOfByte);
		setUPOSMode(ptr);// getPrinter());
	}

	public static synchronized void sendDirectIOCommand(POSPrinter paramPOSPrinter, byte[] paramArrayOfByte)
	{
		sysprint("sendDirectIO : " + toFormatedHexString(paramArrayOfByte));
		try
		{
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

	public static void setUPOSMode(POSPrinter paramPOSPrinter)
	{
		int[] arrayOfInt = { 0, 0, 0, 0 };
		try
		{
			paramPOSPrinter.directIO(1000, arrayOfInt, null);
			if (arrayOfInt[0] == 1)
			{
				delay(200L);
				paramPOSPrinter.setDeviceEnabled(false);
				paramPOSPrinter.directIO(1001, SET_UPOS_MODE, null);
				paramPOSPrinter.setDeviceEnabled(true);
			}
		}
		catch (JposException localJposException)
		{
			sysprint(localJposException.getMessage());
		}
	}
	
	  public boolean verifyPrinterOnline(int paramInt)
	  {
	    this.directIOEventState = 4;
	    int i = 0;
	    this.waitStatusAfterReset = false;
	    do
	    {
	      sendDirectIOCommand(getPrinter(), PRINTER_SATUS);
	      delay(1000L);
	      i++;
	    } while ((!this.waitStatusAfterReset) && (i < paramInt));
	    this.directIOEventState = 1;
	    return this.waitStatusAfterReset;
	  }

		boolean firstRun=true;
	private void directIOEventOccurredNormalState(DirectIOEvent paramDirectIOEvent)
	  {
	      if (this.firstRun)
	      {
	        byte[] arrayOfByte = (byte[])paramDirectIOEvent.getObject();
	        sysprint(paramDirectIOEvent.toString());
	        sysprint(toFormatedHexString(arrayOfByte));
	      }
	        /*
	        if (arrayOfByte[0] == -1) {
	          dataProcess((byte[])paramDirectIOEvent.getObject());
	        }
	      }
	      else
	      {
	        dataProcess((byte[])paramDirectIOEvent.getObject());
	      }
	      */
	  }
	  
	  private void directIOEventOccurredHeadHealthState(DirectIOEvent paramDirectIOEvent)
	  {
	    byte[] arrayOfByte = (byte[])paramDirectIOEvent.getObject();
        sysprint(paramDirectIOEvent.toString());
        sysprint(toFormatedHexString(arrayOfByte));
        /*
	    int i = 0;
	    if ((this.status.isTI1xR_compat()) || (this.status.isTI2xR_compat())) {
	      i += 5;
	    }
	    if (!this.bus.equalsIgnoreCase("USB")) {
	      i += 5;
	    }
	    if (arrayOfByte.length > 21 - i) {
	      processHeadHealthCmd(arrayOfByte);
	    }*/
	  }
	  
	  private void directIOEventOccurredDeviceId(DirectIOEvent paramDirectIOEvent)
	  {
	    byte[] arrayOfByte1 = (byte[])paramDirectIOEvent.getObject();
        sysprint(paramDirectIOEvent.toString());
        sysprint(toFormatedHexString(arrayOfByte1));
	  }
	  private void directIOEventOccurredKn4(DirectIOEvent paramDirectIOEvent)
	  {
	    byte[] arrayOfByte = (byte[])paramDirectIOEvent.getObject();
        sysprint(paramDirectIOEvent.toString());
        sysprint(toFormatedHexString(arrayOfByte));
	    /*
        if (arrayOfByte.length >= 3) {
	      this.kn4UserFlashReady = Status.getBitValue(arrayOfByte[2], 7);
	    }
	    */
	  }
	  private void directIOEventOccurredStatusAfterReset(DirectIOEvent paramDirectIOEvent)
	  {
	    this.waitStatusAfterReset = true;
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
	//##############################################################
    public static String dumpClassPath() {
    	String s="";
        String classpath = System.getProperty("java.class.path");
        String[] classPathValues = classpath.split(File.pathSeparator);
        for (String classPath: classPathValues) {
            s += classPath + "\n"; // System.out.println(classPath);
        }
        return s;
    }
}
