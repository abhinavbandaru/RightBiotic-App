package com.example.rightbiotic;

import java.util.Arrays;

public class Calculation {

// Resutlts for respective Panel of Antibiotics.
    char[] Panel_Result = new char[7]; // shall contain Ex 'R','S', 'I'
    char[] Panel1_Result = new char[7];
    char[] Panel2_Result = new char[7];
    char[] Panel3_Result = new char[7];
    char[] Panel4_Result = new char[7];
    char[] Panel5_Result = new char[7];
    char[] Panel6_Result = new char[7];
    char[] Panel7_Result = new char[7];
    char[] Panel8_Result = new char[7];

    float[] PP1_450nm = new float[8], PP2_450nm = new float[8], PP3_450nm = new float[8], PP4_450nm = new float[8], PP5_450nm = new float[8], PP6_450nm = new float[8], PP7_450nm = new float[8], PP8_450nm = new float[8];
    float[] PP1_630nm = new float[8], PP2_630nm = new float[8], PP3_630nm = new float[8], PP4_630nm = new float[8], PP5_630nm = new float[8], PP6_630nm = new float[8], PP7_630nm = new float[8], PP8_630nm = new float[8];

    float[] Pi_450nm = new float[8]; float[] PiN_450nm = new float[8];
    float[] Pi_630nm = new float[8]; float[] PiN_630nm = new float[8];

// Panel Antibiotics Name for AST
//Original
//String Panel1[] = {"Co-Trimexazole", "Teicoplanin", "Meropenem","Cloxacillin", "Ceftazidime", "Clindamycin", "Linezolid"};
//String Panel2[] = {"Moxifloxacin", "Nitrofurantoin", "Lincomycin", "Netilmicin", "Vancomycin", "Tigecycline", "Azithromycin"};
//John Hopkins
//String Panel1[] = {"Amoxycillin", "Nitrofurantoin", "Cephalexin", "Cefixime", "Azithromycin", "Cefuroxime", "Clindamycin"};
//String Panel2[] = {"Amoxyclavulanic Acid", "Cefotaxime", "Ceftriaxone", "Ceftazidime", "Fosfomycin", "Sultamicillin", "Ticarnic"};
//Lab
    String[] Panel1 = {"Co-Trimexazole", "Teicoplanin", "Meropenem","Cloxacillin", "Ceftazidime", "Clindamycin", "Linezolid"};
    String[] Panel2 = {"Amoxiclav", "Nitrofurantoin", "Cephalexin", "Cefixime", "Azithromycin", "Ceftazidime", "Clindamycin"};

    String[] Panel3 = {"Amoxycillin", "Gentamicin", "Amikacin", "Cefepime", "Ofloxacin", "Ciprofloxacin", "Ceftriaxone"};
    String[] Panel4 = {"Pip-Tazobac", "Cefotaxime", "Cefuroxime", "Tobramycin", "Levofloxacin", "Cefazolin", "Imipenem"};

    String[] Panel5 = {"Netilmicin", "Azithromycin", "Sparfloxacin", "Erythromycin", "Cefaperazone+Sulb", "Polymyxin-B", "Colistin"};
    String[] Panel6 = {"", "Norfloxacin", "Cefixime", "", "Cefadroxil", "Aztreonam", "Prulifloxacin"};

     int No_of_Mins=120;  // variable to hold the Time elapsed between Pi0 and PiN

     int test_type_index=1;  // variable to hold the test type index (1-10)
     int program_type_index;


/*
Index value for each test type
	1-URINE
	2-Peritoneal fluid
	3-SPUTUM
	4-BLOOD
	5-PUS
	6-CSF
	7-Ascites
	8-Scrapping
	9-SWAB
	10-OTHERS */

    // case 11: variables
    int strip1_status=0;
    int strip2_status=0;
    int strip3_status=0;
    int strip4_status=0;
    int strip5_status=0;
    int strip6_status=0;
    int strip7_status=0;
    int strip8_status=0;

     String PiResults = "";				//String array to hold the Pi results Any four cases.
     String[] AST_Results = new String[5];  // Row 0 -> Organism Name Ex: E.Coli, Pseudo, Staph,Klebsiella,Entero,Mix
    //  Row 1 -> Colony count  Ex: 10^4 cell/ml or 10^5 cells/ml.

     char[] AST_Bacteria = new char[30];
     char Mode=0;  		//0 for Mono chromatic or 1 for BiChromatic
    int roomTemperature = 27;

    Calculation(float[] PP1_450nm, float[] PP2_450nm, float[] PP3_450nm, float[] PP4_450nm, float[] PP5_450nm, float[] PP6_450nm, float[] PP7_450nm, float[] PP8_450nm,
                float[] PP1_630nm, float[] PP2_630nm, float[] PP3_630nm, float[] PP4_630nm, float[] PP5_630nm, float[] PP6_630nm, float[] PP7_630nm, float[] PP8_630nm,
                float[] Pi_450nm, float[] PiN_450nm, float[] Pi_630nm, float[] PiN_630nm){
        this.PP1_450nm = PP1_450nm; this.PP2_450nm = PP2_450nm; this.PP3_450nm = PP3_450nm;
        this.PP4_450nm = PP4_450nm; this.PP5_450nm = PP5_450nm;
        this.PP6_450nm = PP6_450nm; this.PP7_450nm = PP7_450nm; this.PP8_450nm = PP8_450nm;
        this.PP1_630nm = PP1_630nm; this.PP2_630nm = PP2_630nm; this.PP3_630nm = PP3_630nm;
        this.PP4_630nm = PP4_630nm; this.PP5_630nm = PP5_630nm;
        this.PP6_630nm = PP6_630nm; this.PP7_630nm = PP7_630nm; this.PP8_630nm = PP8_630nm;
        this.Pi_450nm = Pi_450nm; this.PiN_450nm = PiN_450nm; this.Pi_630nm = Pi_630nm; this.PiN_630nm = PiN_630nm;
        EstimateResults();
        System.out.println(Arrays.toString(Panel_Result));
        System.out.println(Arrays.toString(Panel1_Result));
        System.out.println(Arrays.toString(Panel2_Result));
        System.out.println(Arrays.toString(Panel3_Result));
        System.out.println(Arrays.toString(Panel4_Result));
        System.out.println(Arrays.toString(Panel5_Result));
        System.out.println(Arrays.toString(Panel6_Result));
        System.out.println(Arrays.toString(Panel7_Result));
        System.out.println(Arrays.toString(Panel8_Result));
    }

    void ValidatePi()  // Input the No_of_Mins Mono or Bichro and populated array Pi and PiN
    {

//	switch(No_of_Mins)
//	{
//		case 1:
//			strcpy(PiResults, "UTI GRAM +VE");			
//			break;
//		case 2:
//			strcpy(PiResults, "UTI GRAM -VE");		
//			break;
//		case 3:			
//			strcpy(PiResults, "INCUBATE 1 HOUR");		
//			break;
//		case 4:
//			strcpy(PiResults, "ABORT");			
//			break;		
//	}

        //Assume the strip reading upto 2 hrs
        if (No_of_Mins >= 120 && No_of_Mins < 180)
        {
            //check for well 1 value

            if(PiN_450nm[0] < 0.5)
            {
                PiResults = "INCUBATE 1 HOUR";
                return;
            }
            else
            {
                //GetPiResult();
                PiResults = "BACTERIA PRESENT"; //Bacteria Present Abort
                return;
            }
        }

        if (No_of_Mins >= 180 && No_of_Mins < 300)
        {
            //check for well 1 value

            if(PiN_450nm[0] < 0.5)
            {
                PiResults = "ABORT";
            }
            else
            {
                PiResults = "BACTERIA PRESENT"; //Bacteria Present Abort
                //GetPiResult();
            }
        }
    }

    void GetPiResult()
    {
        int pi_max_cell=0;
        float pi_max_value = 0;
        int i;

        pi_max_value = PiN_450nm[0];

        for(i=1;i<8;i++)
        {
            if (pi_max_value < PiN_450nm[i])
            {
                pi_max_cell = i;
                pi_max_value = PiN_450nm[i];
            }
        }

        if(pi_max_cell==3 || pi_max_cell==4 || pi_max_cell==7)
        {
            PiResults = "BACTERIA PRESENT"; //Bacteria Present Abort
        }
        else
        {
            PiResults = "BACTERIA PRESENT";
        }



    }
    void GetBacteria()
    {

        int pi_450_min_cell=0;
        float pi_450_min_value = 0;

        int pi_630_max_cell=0;
        float pi_630_max_value = 0;

        int pi_630_min_cell=0;
        float pi_630_min_value = 0;

        float higher_limit, lower_limit;

        int pi_450_max_cell=0;
        float pi_450_max_value = 0;
        float a;
        int i,j,n;
        float[] pi_450_value_asc = new float[8];
        int[] pi_450_cell_asc = new int[8];

        float[] pi_450_630_value = new float[8];
        float[] pi_450_630_value_asc = new float[8];
        int[] pi_450_630_cell_asc = new int[8];

        float[] pi_630_value_asc = new float[8];
        int[] pi_630_cell_asc = new int[8];
        int[] pi_450_cell_limit = new int[8];
        int[] pi_630_cell_limit = new int[8];

//		float x;
//		float diff;
        int isEntero = 0;
        int isPseudo = 0;
        int isStaph = 0;
        int isKleb = 0;
        int isEColi = 0;
        int isGrampos = 0;
        int isGramneg = 0;

        int Well8High = 0, Well5High = 0, Well6High = 0, Well7High = 0;

        //450 nm ascending
        for (i = 0; i < 8; i++)
        {
            pi_450_value_asc[i] = Pi_450nm[i];
            pi_450_cell_asc[i] = i;
        }

        for (i = 0; i < 8; ++i)
        {
            for (j = i + 1; j < 8; ++j)
            {
                if (pi_450_value_asc[i] > pi_450_value_asc[j])
                {
                    a =  pi_450_value_asc[i];
                    pi_450_value_asc[i] = pi_450_value_asc[j];
                    pi_450_value_asc[j] = a;

                    n = pi_450_cell_asc[i];
                    pi_450_cell_asc[i] = pi_450_cell_asc[j];
                    pi_450_cell_asc[j] = n;
                }
            }
        }

        //Get the Max Value of 450
        pi_450_max_value = pi_450_value_asc[7];
        pi_450_max_cell = pi_450_cell_asc[7];

        //Get the Min Value of 450
        pi_450_min_value = pi_450_value_asc[0];
        pi_450_min_cell = pi_450_cell_asc[0];

        higher_limit=(8*pi_450_max_value)/100;
        lower_limit=(8*pi_450_min_value)/100;

        //Set the Limit

        for(i=0;i<8;i++)
        {
            if(pi_450_max_value-Pi_450nm[i] > higher_limit)
            {
                pi_450_cell_limit[i] = 0;
            }
            else if(pi_450_max_value-Pi_450nm[i] == 0)
            {
                pi_450_cell_limit[i] = 100;
            }
            else
            {
                pi_450_cell_limit[i] = 1;
            }
        }
        //Set the lower limit
        for(i=0;i<8;i++)
        {
            if (pi_450_cell_limit[i] != 1 && pi_450_cell_limit[i] != 100)
            {
                if(Pi_450nm[i]-pi_450_min_value > lower_limit)
                {
                    pi_450_cell_limit[i] = 0;
                }
                else if(pi_450_min_value-Pi_450nm[i] == 0)
                {
                    pi_450_cell_limit[i] = -100;
                }
                else
                {
                    pi_450_cell_limit[i] = -1;
                }
            }
        }

        // 630nm ascending
        for (i = 0; i < 8; i++)
        {
            pi_630_value_asc[i] = Pi_630nm[i];
            pi_630_cell_asc[i] = i;
        }

        for (i = 0; i < 8; ++i)
        {
            for (j = i + 1; j < 8; ++j)
            {
                if (pi_630_value_asc[i] > pi_630_value_asc[j])
                {
                    a =  pi_630_value_asc[i];
                    pi_630_value_asc[i] = pi_630_value_asc[j];
                    pi_630_value_asc[j] = a;

                    n = pi_630_cell_asc[i];
                    pi_630_cell_asc[i] = pi_630_cell_asc[j];
                    pi_630_cell_asc[j] = n;
                }
            }
        }
        //Get the Max Value
        pi_630_max_value = pi_630_value_asc[7];
        pi_630_max_cell = pi_630_cell_asc[7];

        //Get the Min Value
        pi_630_min_value = pi_630_value_asc[0];
        pi_630_min_cell = pi_630_cell_asc[0];

        higher_limit=(8*pi_630_max_value)/100;
        lower_limit=(8*pi_630_min_value)/100;

        //Set the Limit

        for(i=0;i<8;i++)
        {
            if(pi_630_max_value-Pi_630nm[i] > higher_limit)
            {
                pi_630_cell_limit[i] = 0;
            }
            else if(pi_630_max_value-Pi_630nm[i] == 0)
            {
                pi_630_cell_limit[i] = 100;
            }
            else
            {
                pi_630_cell_limit[i] = 1;
            }
        }
        //Set the lower limit
        for(i=0;i<8;i++)
        {
            if (pi_630_cell_limit[i] != 1 && pi_630_cell_limit[i] != 100)
            {
                if(Pi_630nm[i]-pi_630_min_value > lower_limit)
                {
                    pi_630_cell_limit[i] = 0;
                }
                else if(pi_630_min_value-Pi_630nm[i] == 0)
                {
                    pi_630_cell_limit[i] = -100;
                }
                else
                {
                    pi_630_cell_limit[i] = -1;
                }
            }
        }


        //450/630 ratio ascending
        for(i=0;i<8;i++)
        {
            pi_450_630_value[i] = Pi_450nm[i]/Pi_630nm[i];
            pi_450_630_value_asc[i] = Pi_450nm[i]/Pi_630nm[i];
            pi_450_630_cell_asc[i] = i;
        }

        for (i = 0; i < 8; ++i)
        {
            for (j = i + 1; j < 8; ++j)
            {
                if (pi_450_630_value_asc[i] > pi_450_630_value_asc[j])
                {
                    a =  pi_450_630_value_asc[i];
                    pi_450_630_value_asc[i] = pi_450_630_value_asc[j];
                    pi_450_630_value_asc[j] = a;

                    n = pi_450_630_cell_asc[i];
                    pi_450_630_cell_asc[i] = pi_450_630_cell_asc[j];
                    pi_450_630_cell_asc[j] = n;
                }
            }
        }



        if((pi_450_cell_limit[3] == 1 || pi_450_cell_limit[3] == 100 || pi_450_cell_limit[7] == 1 || pi_450_cell_limit[7] == 100) && (pi_450_cell_limit[0] == -1 || pi_450_cell_limit[0] == -100 || pi_450_cell_limit[2] == -1 || pi_450_cell_limit[2] == -100))
        {
            AST_Results[0] = "E.Coli Gram-ve";
            return;
        }

        if((pi_630_cell_limit[4] == 1 || pi_630_cell_limit[4] == 100 || pi_630_cell_limit[7] == 1 || pi_630_cell_limit[7] == 100) && (pi_630_cell_limit[0] == -1 || pi_630_cell_limit[0] == -100 || pi_630_cell_limit[2] == -1 || pi_630_cell_limit[2] == -100))
        {
            AST_Results[0] = "E.Coli Gram-ve";
            return;
        }
        //630 - well 5 or 1 high And Well 3 or 6 low 
        //Or 450 - well 5 high 

        if((pi_630_cell_limit[0] == 1 || pi_630_cell_limit[0] == 100 || pi_630_cell_limit[4] == 1 || pi_630_cell_limit[4] == 100) && (pi_630_cell_limit[2] == -1 || pi_630_cell_limit[2] == -100 || pi_630_cell_limit[5] == -1 || pi_630_cell_limit[5] == -100) || (pi_450_cell_limit[4] == 1 || pi_450_cell_limit[4] == 100))
        {
            AST_Results[0] = "Kleb Gram-ve";
            return;
        }



/**********************************09-02-2020-New Logic *******************************************/

//			if(pi_450_cell_asc[0] == 6)
//			{
//					strcpy(AST_Results[0], "CONTAMINATION??");
//					return;
//			}

        if(pi_450_cell_limit[3] == -1 || pi_450_cell_limit[3] == -100)
        {
            AST_Results[0] = "Acineto or Citro";
            return;
        }

        if(pi_450_cell_limit[5] == 1 || pi_450_cell_limit[5] == 100)
        {
            AST_Results[0] = "Entero Gram-ve";
            return;
        }

        if(pi_450_cell_asc[0] == 4)
        {
            AST_Results[0] = "Strepto Gram+ve";
            return;
        }

//			if(pi_450_cell_asc[7] == 5)
//			{
//					strcpy(AST_Results[0], "Enterobact Gram+ve");
//					return;
//			}			
//			
        if(pi_450_cell_asc[7] == 0 && pi_450_cell_asc[7] == 1 && pi_450_cell_asc[7] == 3)
        {
            AST_Results[0] = "Pseudo Gram-ve";
            return;
        }

        if(pi_450_cell_asc[7] == 0 && pi_450_cell_asc[7] == 1 && pi_450_cell_asc[7] == 8)
        {
            AST_Results[0] = "Pseudo Gram-ve";
            return;
        }

//			if(pi_450_cell_asc[0] == 0 && pi_450_cell_asc[7] == 4)
//			{
//					strcpy(AST_Results[0], "E.Coli Gram-ve");
//					return;
//			}	
//			
//			if(pi_450_cell_asc[0] == 0 && pi_450_cell_asc[7] == 6)
//			{
//					strcpy(AST_Results[0], "E.Coli Gram-ve");
//					return;
//			}	
//			
//			if(pi_450_cell_asc[0] == 0 && pi_450_cell_asc[7] == 7)
//			{
//					strcpy(AST_Results[0], "Kleb Gram-ve");
//					return;
//			}	
//			
//			if(pi_450_cell_asc[0] == 1 && pi_450_cell_asc[7] == 7)
//			{
//					strcpy(AST_Results[0], "Kleb Gram-ve");
//					return;
//			}	

        if(pi_450_cell_asc[0] == 7)
        {
            AST_Results[0] = "Staph Gram+ve";
            return;
        }

        if(pi_450_cell_asc[7] == 0 && pi_450_cell_asc[7] == 1 && pi_450_cell_asc[7] == 2)
        {
            AST_Results[0] = "Staph Gram+ve";
            return;
        }

        if(pi_450_cell_asc[7] == 1 && pi_450_cell_asc[7] == 4 && pi_450_cell_asc[7] == 6)
        {
            AST_Results[0] = "Entero Gram+ve";
            return;
        }
        if(pi_450_cell_asc[7] == 0 && pi_450_cell_asc[0] == 5)
        {
            AST_Results[0] = "Proteus Gram-ve";
            return;
        }

        if(pi_450_cell_asc[7] == 0 && pi_450_cell_asc[0] == 6)
        {
            AST_Results[0] = "Proteus Gram-ve";
            return;
        }

        if(pi_450_cell_asc[7] == 0 && pi_450_cell_asc[0] == 7)
        {
            AST_Results[0] = "Proteus Gram-ve";
            return;
        }

        AST_Results[0] = "Others";

    }
    float round(float var)
    {
        // 37.66666 * 100 =3766.66 
        // 3766.66 + .5 =37.6716    for rounding off value 
        // then type cast to int so value is 3766 
        // then divided by 100 so the value converted into 37.66 
        float value = (int)(var * 10 + .5);
        return (float)value / 10;
    }
    void EstimateResults()  // Inputs are Mono or Bichromo and Pi with Populate array P1 ... P6  and strip1_status to tell you which array is to be read
    {
        int i =0, y =0, z = 0;
//	if(Mode == 0)
//	{
//		strcpy(AST_Results[0],"Mixed");
//		strcpy(AST_Results[1],"10^4 Cell/ml");
//	}
//	else
//	{
//	  sprintf (AST_Results[0], "%d\n", No_of_Mins);
//		strcpy(AST_Results[1],"10^5 Cells/ml");
//	return;
//	}


        AST_Results[0] = "\0";
        AST_Results[2] = "\0";
        AST_Results[3] = "\0";
        AST_Results[4] = "N"; //if Y print antibiotic name else do not print antibiotic name.

        //Assume the strip reading upto 4 hrs
        if (No_of_Mins >= 240 && No_of_Mins < 300)
        {
            //check for well 1 value

            if(Pi_450nm[0] < 0.27)
            {
                AST_Results[0]= "INCUBATE 1 HOUR";
                return;
            }
        }

        //Assume the strip reading upto 2 hrs
        if (No_of_Mins >= 300 && No_of_Mins < 360)
        {
            //check for well 1 value

            if(Pi_450nm[0] < 0.27)
            {
                AST_Results[0]= "INCUBATE 1 MORE HOUR";
                return;
            }
        }

        if (No_of_Mins >= 360)
        {
            //check for well 1 value

            if(Pi_450nm[0] < 0.27)
            {
                AST_Results[0] = "BACTERIA ABSENT";
                AST_Results[1]= "\0";
                return;
            }
        }

        if(Pi_450nm[0] < 0.27)
        {
            AST_Results[0] = "BACTERIA ABSENT";
            AST_Results[1]= "\0";
            return;
        }

        GetBacteria();

        if (Pi_450nm[0] >= 0.4)
        {
            AST_Results[1]="10^5 Cell/ml";
        }
        else
        {
            AST_Results[1]="10^4 Cell/ml";
        }

        fillresults();

    }

    float percent_difference(float Y1, float Y2)
    {
        float diff = Math.abs(Y2-Y1);
        float percent = (diff/Y1)*100;
        return percent;
    }

    ///////////////////////////////////////////////////////////////
// INPUT: PARAMETER NAME: ARR[], N
// OUTPUT: FLOAT STD_DEV.
///////////////////////////////////////////////////////////////
    double stand_dev_p(float arr[], int n)
    {
        // find the mean.
        int i=0;
        int size = n;
        float mean=0;
        float num=0;
        float tmp=0;
        double std=0.0;
        for(i=0; i < size; i++)
        {
            mean = mean+arr[i];		// finding the sum of all elements in the given arrary
        }
        mean = mean / n;				// find the meam
        /// square (x-xbar)

        for(i=0; i < size; i++)
        {
            tmp = arr[i]-mean;
            if(mean == 0) continue;
            num += Math.pow(tmp,2);		// finding thes quare of (x-xbar) and simultaneously find the sum
        }
        num = num / n;
        std = Math.sqrt(num);  // Square root sum of all (x-xbar)/n
        return std;
    }

    void get_strip_result(float arr[])
    {
        float z=0, PP;
        int i;
        float ratio_p1 = 0;
        String Tex = "";

        PP = 0;
        for(i=1; i<8; i++)
        {
            if(arr[0] < arr[i])
            {
                PP = PP + arr[i];
                z++;
            }
        }
        PP = (PP + arr[0])/(z+1);

        for(i = 0; i<7; i++)
        {
            ratio_p1 = PP/arr[i+1];
            if (ratio_p1 < 1.14)
            {
                Panel_Result[i] = 'R';
            }
            else if (ratio_p1 >= 1.14 && ratio_p1 < 1.23)
            {
                Panel_Result[i] = 'I';
            }
            else if (ratio_p1 >= 1.23)
            {
                Panel_Result[i] = 'S';
            }
        }
    }

    void fillresults()
    {
        int i;
        int isNotResistent = 0;
        AST_Results[4]= "Y";
        if (strip1_status == 1)
        {
            //P2 450nm strip	
            get_strip_result(PP1_450nm);

            for(i = 0; i<7; i++)
            {
                Panel1_Result[i] = Panel_Result[i];

                if(Panel_Result[i] != 'R')
                {
                    isNotResistent ++;
                }
            }
        }

        if (strip2_status == 1)
        {
            //P3 450nm strip	
            get_strip_result(PP2_450nm);

            for(i = 0; i<7; i++)
            {
                Panel2_Result[i] = Panel_Result[i];
                if(Panel_Result[i] != 'R')
                {
                    isNotResistent ++;
                }
            }
        }

        if (strip3_status == 1)
        {
            //P4 450nm strip	
            get_strip_result(PP3_450nm);

            for(i = 0; i<7; i++)
            {
                Panel3_Result[i] = Panel_Result[i];
                if(Panel_Result[i] != 'R')
                {
                    isNotResistent ++;
                }
            }
        }

        if (strip4_status == 1)
        {
            //P5 450nm strip	
            get_strip_result(PP4_450nm);

            for(i = 0; i<7; i++)
            {
                Panel4_Result[i] = Panel_Result[i];
                if(Panel_Result[i] != 'R')
                {
                    isNotResistent ++;
                }
            }
        }

        if (strip5_status == 1)
        {
            //P6 450nm strip	
            get_strip_result(PP5_450nm);

            for(i = 0; i<7; i++)
            {
                Panel5_Result[i] = Panel_Result[i];
                if(Panel_Result[i] != 'R')
                {
                    isNotResistent ++;
                }
            }
        }

        if (strip6_status == 1)
        {
            //P7 450nm strip	
            get_strip_result(PP6_450nm);

            for(i = 0; i<7; i++)
            {
                Panel6_Result[i] = Panel_Result[i];
                if(Panel_Result[i] != 'R')
                {
                    isNotResistent ++;
                }
            }
        }

        if (strip7_status == 1)
        {
            //P7 450nm strip	
            get_strip_result(PP7_450nm);

            for(i = 0; i<7; i++)
            {
                Panel7_Result[i] = Panel_Result[i];
                if(Panel_Result[i] != 'R')
                {
                    isNotResistent ++;
                }
            }
        }

        if (strip8_status == 1)
        {
            //P7 450nm strip	
            get_strip_result(PP8_450nm);

            for(i = 0; i<7; i++)
            {
                Panel8_Result[i] = Panel_Result[i];
                if(Panel_Result[i] != 'R')
                {
                    isNotResistent ++;
                }
            }
        }

        if (isNotResistent == 0)
        {
            AST_Results[0]= "CONTAMINATION??";
            AST_Results[1]= "\0";
            AST_Results[4]= "N";
//		strcpy(AST_Results[4], "N"); //if Y print antibiotic name else do not print antibiotic name.


            for(i = 0; i<7; i++)
            {
                Panel1_Result[i] = '\0';
                Panel2_Result[i] = '\0';
                Panel3_Result[i] = '\0';
                Panel4_Result[i] = '\0';
                Panel5_Result[i] = '\0';
                Panel6_Result[i] = '\0';
                Panel7_Result[i] = '\0';
                Panel8_Result[i] = '\0';
            }
        }
        //sprintf (AST_Results[0], "%d\n", isNotResistent);
    }
}
