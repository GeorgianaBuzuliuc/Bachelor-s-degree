/*
VO2Max (maximal oxygen consumption) is the maximum capacity of an individual's body to transport and use oxygen during incremental exercise, 
which reflects the physical fitness of the individual. The name is derived from V - volume, O2 - oxygen, max - maximum.

Gross calorie burn is the absolute total amount of calories burned while performing any given activity. It includes the calories that your 
body burned specifically to perform the activity itself, plus the additional calories burned throughout the duration of the physical activity 
that your body must continually burn at all times to digest food, keep tissues alive, and support the function of vital organs. The extra calories 
burned in addition to those required for physical activity are collectively known as your Resting Metabolic Rate (RMR), and they represent the calories 
that your body would have burned anyway, even if you had just been relaxing on the couch and not performing any physical activity. 

Now that you know what gross calorie burn is, net calorie burn is easily understood. Net calorie burn, for any given physical activity, is the amount 
of calories burned only to perform the physical activity, and no more. As opposed to gross calorie burn, net calorie burn does not include calories burned 
to support your RMR.
*/


import java.util.*;

public class Calories{
	
//=================================== PRIVATE DATA ====================================
public static String gender = "female";					//	"male" or "female"
public static Date bd = new Date(00, 11, 27, 10, 00);
public static double age = getAgeFromDateOfBirth(bd);	//	my age		[years]
public static double weight = 60;						//	my weight 	[kg]
public static double height = 162.0;					//	my height 	[cm] 
public static double VO2max = 40;						//	my VO2Max (maximal oxygen consumption) 					[ml/kg/min]
public static double AHR = 90;							//  my AHR (Average Heart Rate in activiy duration) 		[beats/minute]
public static double backpack_weight = 22;				//	weight of my backpack 	[kg]

//=================================== PRIVATE DATA ====================================



//=====================================================================================
//================================= SELECTED METHODS ==================================
//=====================================================================================

public static double getUpCalories (double distance_metri, double avg_slope_procent, double moving_time_seconds){
	return totalUpCalories (gender, age, weight, height, backpack_weight, distance_metri/1000., avg_slope_procent, moving_time_seconds);
}

public static double getDownCalories (double distance_metri, double avg_slope_procent, double moving_time_seconds){
	return totalDownCalories (gender, age, weight, height, backpack_weight, distance_metri/1000., avg_slope_procent, moving_time_seconds);
}

public static double getUpLostWeight(double distance_metri, double avg_slope_procent, double moving_time_seconds){
	return getLostWeight (getUpCalories (distance_metri/1000., avg_slope_procent, moving_time_seconds));
}

public static double getDownLostWeight(double distance_metri, double avg_slope_procent, double moving_time_seconds){
	return getLostWeight (getDownCalories (distance_metri/1000., avg_slope_procent, moving_time_seconds));
}

//=====================================================================================
//=====================================================================================
//=====================================================================================



//return Basal Metabolic Rate (in calories burned per 24 hours)
public static double getBMR(){
	return getBMR(gender, age, weight, height);
}

//return Resting Metabolic Rate Calorie Burn 
public static double getRMRCB(double seconds) {	
	return getRMRCB(gender, age, weight, height, seconds);			
}

//return Total Calories Burn in activiy duration (for unknown VO2max)
public static double getTotalCalories(double moving_time_seconds) {				
	return getTotalCalories(gender, age, weight, moving_time_seconds, AHR);
}

//return Total Calories Burn in activiy duration (for known VO2max)
public static double getTotalCaloriesVO2(double moving_time_seconds) {	
	return getTotalCaloriesVO2(gender, age, weight, moving_time_seconds, AHR, VO2max);
}

//return Net Calories Burn in activiy duration (for unknown VO2max)
public static double getNetCalories(double moving_time_seconds) {	
	double RMRCB = getRMRCB(moving_time_seconds);			
	double GCB = getTotalCalories(gender, age, weight, moving_time_seconds, AHR);
	return GCB - RMRCB;
}

//return Net Calories Burn in activiy duration (for known VO2max)
public static double getNetCaloriesVO2(double moving_time_seconds) {	
	double RMRCB = getRMRCB(moving_time_seconds);			
	double GCB = getTotalCaloriesVO2(gender, age, weight, moving_time_seconds, AHR, VO2max);
	return GCB - RMRCB;
}

//*************************************************************************************

public static double totalCalories (double distance, double avg_slope_procent, double moving_time_seconds){
	return totalCalories(gender, age, weight, height, backpack_weight, distance, avg_slope_procent, moving_time_seconds);
}
	
public static double totalLostWeight (double distance, double avg_slope_procent, double moving_time_seconds){
	return totalLostWeight(gender, age, weight, height, backpack_weight, distance, avg_slope_procent, moving_time_seconds);
}

//*************************************************************************************

public static double calculateEnergyExpenditure(double durationInSeconds, double distance) {
	return calculateEnergyExpenditure(gender, age, weight, height, durationInSeconds, distance);
}

//*************************************************************************************

public static double PandolfMR (double hikingSpeed, double slope, double terrain_factor){
	return PandolfMR (weight, backpack_weight, hikingSpeed, slope, terrain_factor);
}

//*************************************************************************************

/*
activity = cross_country, up_hill_down, up_hill_only, down_hill_only

incline = 1 for slope 1% to 5%
incline = 5 for slope 5% to 10%
incline = 10 for slope > 10%
*/
public static double calcCC(double distance, double minutes, String activity, int incline) {
	return calcCC(weight, backpack_weight, distance, minutes, activity, incline);
}



//====================================== METODA 1 =====================================
//=====================================================================================

//return Net Calories Burn in activiy duration (for unknown VO2max)
public static double getNetCalories(String gender, double age, double kilogramweight, double cmheight, double seconds, double AHR) {	
	double RMRCB = getRMRCB(gender, age, kilogramweight, cmheight, seconds);				
	double GCB = getTotalCalories(gender, age, kilogramweight, seconds, AHR);
	return GCB - RMRCB;
}

//return Net Calories Burn in activiy duration (for known VO2max)
public static double getNetCaloriesVO2(String gender, double age, double kilogramweight, double cmheight, double seconds, double AHR, double VO2max) {	
	//Resting Metabolic Rate Calorie Burn 
	double RMRCB = getRMRCB(gender, age, kilogramweight, cmheight, seconds);			
	double GCB = getTotalCaloriesVO2(gender, age, kilogramweight, seconds, AHR, VO2max);
	return GCB - RMRCB;
}

//return Basal Metabolic Rate (in calories burned per 24 hours)
public static double getBMR(String gender, double age, double kilogramweight, double cmheight){
	double BMR = 0;
	if(gender.equals("male"))
		BMR = 13.75*kilogramweight + 5*cmheight - 6.76*age + 66;
	else if(gender.equals("female"))
		BMR = 9.56*kilogramweight + 1.85*cmheight - 4.68*age + 655;
	return BMR;
}

//return Resting Metabolic Rate Calorie Burn 
public static double getRMRCB(String gender, double age, double kilogramweight, double cmheight, double seconds){
	double hours = seconds/3600.;
	double BMR	= getBMR(gender, age, kilogramweight, cmheight);
	return ((BMR * 1.1) / 24.) * hours;
}

//return Maximum Heart Rate 
public static double getMaxHR(double age){
	return 208. - 0.7*age;
}

//return Total Calories Burn in activiy duration (for unknown VO2max)
//Heart Rate Based Calories Burn Calculator (for unknown VO2max)
//AHR = Average Heart Rate in activiy duration [beats/minute]	 
public static double getTotalCalories(String gender, double age, double kilogramweight, double seconds, double AHR) {
	double hours = seconds/3600.;	
	double caloric_expenditure_per_hour = 0;
	if(gender.equals("male"))
		caloric_expenditure_per_hour = ((-55.0969 + (0.6309*AHR) + (0.1988*kilogramweight) + (0.2017*age))/4.184)*60;
	else if(gender.equals("female"))
		caloric_expenditure_per_hour = ((-20.4022 + (0.4472*AHR) - (0.1263*kilogramweight) + (0.074*age))/4.184)*60;		 
	return caloric_expenditure_per_hour*hours;
}

//return Total Calories Burn in activiy duration (for known VO2max)	 
//Heart Rate Based Calories Burn Calculator (for known VO2max)	
//AHR = Average Heart Rate in activiy duration [beats/minute]
//VO2Max = maximal oxygen consumption	[ml/kg/min]  	  
public static double getTotalCaloriesVO2(String gender, double age, double kilogramweight, double seconds, double AHR, double VO2max) {	   
	double hours = seconds/3600.;	
	double caloric_expenditure_per_hour = 0;	
	if(gender.equals("male"))
		caloric_expenditure_per_hour = ((-59.3954 - 36.3781 + (0.634*AHR) + (0.404*VO2max) + (0.394*kilogramweight) + (0.271*age))/4.184)*60;
	else if(gender.equals("female"))
		caloric_expenditure_per_hour = ((-59.3954 + (0.450*AHR) + (0.380*VO2max) + (0.103*kilogramweight) + (0.274*age))/4.184)*60;	 
	return caloric_expenditure_per_hour*hours;
}




//====================================== METODA 2 =====================================
//=====================================================================================

/*
distance [km]
ascent [m]
avg_slope_procent [%]
total_equivalent [km]
weight [kg]
backpack_weight [kg]
Calories_up_adjusted [kcal]
Calories_down_adjusted [kcal]	
Total_calories_adjusted [kcal]	
weight_lost [kg]
*/

public static double avg_slope_procent (double ascent, double distance){
	return (41010500./205052430.)*ascent/distance;
}	

public static double ascent (double avg_slope_procent, double distance){
	return (205052430./41010500.)*avg_slope_procent*distance;
}	

public static double distance (double ascent, double avg_slope_procent){
	return (41010500./205052430.)*ascent/avg_slope_procent;
}	

public static double equivalent_distance_on_flat_road (double distance, double avg_slope_procent){
	return (1./50.)*distance*(polinom(proc2grade(avg_slope_procent))+50);
}

public static double getDistance (double avg_slope_procent, double distance_on_flat_road){
	return 50.*distance_on_flat_road/(polinom(proc2grade(avg_slope_procent))+50);
}

public static double proc2grade(double procent){
	return Math.asin(procent/100.);
}

public static double polinom (double avg_slope_procent){
	return (2805*Math.pow(avg_slope_procent, 5) - 587*Math.pow(avg_slope_procent, 4) - 768*Math.pow(avg_slope_procent, 3) + 519*Math.pow(avg_slope_procent, 2) + 196*avg_slope_procent);
}

public static double kCalories_moving_up (double weight, double backpack_weight, double distance, double avg_slope_procent)	{	
	//return (0.3333333333333333*(2*backpack_weight + 3*weight)*(0.09733434366360057*weight*(0.16666666666666666*distance*(polinom(proc2grade(avg_slope_procent))+25)))/weight);
	return 1000*((1./3.)*(2.*backpack_weight + 3.*weight)*((2976237./305774600.)*weight*((1./600.)*distance*(polinom(proc2grade(avg_slope_procent))+25)))/weight);
}

public static double kCalories_moving_down (double weight, double backpack_weight, double distance){
	return 1000*((1./3.)*(2.*backpack_weight + 3.*weight)*((992079./1223098400.)*weight*((1./2.)*distance))/weight);
} 

public static double netCalories (double weight, double backpack_weight, double distance, double avg_slope_procent){
	double kcal_up = kCalories_moving_up (weight, backpack_weight, distance, avg_slope_procent);
	double kcal_down = kCalories_moving_down (weight, backpack_weight, distance);
	return kcal_up + kcal_down;
}
	
public static double netLostWeight (double weight, double backpack_weight, double distance, double avg_slope_procent){
	double kcal = netCalories (weight, backpack_weight, distance, avg_slope_procent);
	return getLostWeight(kcal);
}

public static double totalUpCalories (String gender, double age, double weight, double height, double backpack_weight, double distance, double avg_slope_procent, double moving_time_seconds){
	double net_kcal = kCalories_moving_up (weight, backpack_weight, distance, avg_slope_procent);
	double RMRCB = getRMRCB(gender, age, weight, height, moving_time_seconds);
	return net_kcal + RMRCB;
}

public static double totalDownCalories (String gender, double age, double weight, double height, double backpack_weight, double distance, double avg_slope_procent, double moving_time_seconds){
	double net_kcal = kCalories_moving_down (weight, backpack_weight, distance);
	double RMRCB = getRMRCB(gender, age, weight, height, moving_time_seconds);
	return net_kcal + RMRCB;
}

public static double totalCalories (String gender, double age, double weight, double height, double backpack_weight, double distance, double avg_slope_procent, double moving_time_seconds){
	double net_kcal = netCalories (weight, backpack_weight, distance, avg_slope_procent);
	double RMRCB = getRMRCB(gender, age, weight, height, moving_time_seconds);
	return net_kcal + RMRCB;
}
	
public static double totalLostWeight (String gender, double age, double weight, double height, double backpack_weight, double distance, double avg_slope_procent, double moving_time_seconds){
	double kcal = totalCalories (gender, age, weight, height, backpack_weight, distance, avg_slope_procent, moving_time_seconds);
	return getLostWeight(kcal);
}

public static double getLostWeight(double kcal){
	//return (1./7700.)*kcal;     //in [kg[
	return (1./7.7)*kcal;     //in [g]
}


//====================================== METODA 3 =====================================
//=====================================================================================

/**
     * Calculated the energy expenditure for an activity. 
	 * Adapted from the following website https://sites.google.com/site/compendiumofphysicalactivities/corrected-mets
     *
     * @param height               The height in cm.
     * @param age                  The date of birth.
     * @param weight               The weight of the user in kg
     * @param gender               The gender of the user. gender = "male" or "female"
     * @param durationInSeconds    The duration of the activity in seconds.
     * @param distance             The distance in km.
     * @return The number of calories burnt (kCal)
*/
	 
public static double calculateEnergyExpenditure(String gender, double age, double weight, double height, double durationInSeconds, double distance) {
	double harrisBenedictRmR = convertKilocaloriesToMlKmin(harrisBenedictRmr(gender, weight, age, height), weight);
	double hours = durationInSeconds/3600.;
	double speedInMph = distance / (hours * 1.609344);  //viteza in mile/ora
	float metValue = getMetForActivity((float)speedInMph);
	double correctedMets = metValue * (3.5 / harrisBenedictRmR);
	return correctedMets * hours * weight;
}

public static double convertKilocaloriesToMlKmin(double kilocalories, double weightKgs) {
	double kcalMin = kilocalories / 1440.;
	kcalMin /= 5;
	return ((kcalMin / (weightKgs)) * 1000.);
}

/**
     * Gets the MET value for an activity. 
	 * Based on https://sites.google.com/site/compendiumofphysicalactivities/Activity-Categories/walking .
     *
     * @param speedInMph The speed in miles per hour
     * @return The met value.
*/
private static float getMetForActivity(float speedInMph) { //backpack_weight=0
        if (speedInMph < 2.0) {
            return 2.0f;
        } else if (Float.compare(speedInMph, 2.0f) == 0) {
            return 2.8f;
        } else if (Float.compare(speedInMph, 2.0f) > 0 && Float.compare(speedInMph, 2.7f) <= 0) {
            return 3.0f;
        } else if (Float.compare(speedInMph, 2.8f) > 0 && Float.compare(speedInMph, 3.3f) <= 0) {
            return 3.5f;
        } else if (Float.compare(speedInMph, 3.4f) > 0 && Float.compare(speedInMph, 3.5f) <= 0) {
            return 4.3f;
        } else if (Float.compare(speedInMph, 3.5f) > 0 && Float.compare(speedInMph, 4.0f) <= 0) {
            return 5.0f;
        } else if (Float.compare(speedInMph, 4.0f) > 0 && Float.compare(speedInMph, 4.5f) <= 0) {
            return 7.0f;
        } else if (Float.compare(speedInMph, 4.5f) > 0 && Float.compare(speedInMph, 5.0f) <= 0) {
            return 8.3f;
        } else if (Float.compare(speedInMph, 5.0f) > 0) {
            return 9.8f;
        }
        return 0;
}

private static double getMetFromBackpackWeight(double backpack_weight, double avg_slope_procent) {
	if(avg_slope_procent<2){
        if (backpack_weight < 3) 
			return 0;
		else if (backpack_weight < 7)
			return 0;
		else if (backpack_weight < 11) 
			return 0;
		else if (backpack_weight < 17) 
			return 0;		
		else if (backpack_weight < 23) 
			return 0;
		else if (backpack_weight < 29) 
			return 0;		
		else if (backpack_weight < 35)
			return 0;
		else
			return 0;	
	}else if(avg_slope_procent<5){
        if (backpack_weight < 3) 
			return 0;
		else if (backpack_weight < 7)
			return 0;
		else if (backpack_weight < 11) 
			return 0;
		else if (backpack_weight < 17) 
			return 0;		
		else if (backpack_weight < 23) 
			return 0;
		else if (backpack_weight < 29) 
			return 0;		
		else if (backpack_weight < 35)
			return 0;
		else
			return 0;		
	}else if(avg_slope_procent<10){
        if (backpack_weight < 3) 
			return 0;
		else if (backpack_weight < 7)
			return 0;
		else if (backpack_weight < 11) 
			return 0;
		else if (backpack_weight < 17) 
			return 0;		
		else if (backpack_weight < 23) 
			return 0;
		else if (backpack_weight < 29) 
			return 0;		
		else if (backpack_weight < 35)
			return 0;
		else
			return 0;		
	}else if(avg_slope_procent<20){
        if (backpack_weight < 3) 
			return 0;
		else if (backpack_weight < 7)
			return 0;
		else if (backpack_weight < 11) 
			return 0;
		else if (backpack_weight < 17) 
			return 0;		
		else if (backpack_weight < 23) 
			return 0;
		else if (backpack_weight < 29) 
			return 0;		
		else if (backpack_weight < 35)
			return 0;
		else
			return 0;		
	}else if(avg_slope_procent<40){
        if (backpack_weight < 3) 
			return 0;
		else if (backpack_weight < 7)
			return 0;
		else if (backpack_weight < 11) 
			return 0;
		else if (backpack_weight < 17) 
			return 0;		
		else if (backpack_weight < 23) 
			return 0;
		else if (backpack_weight < 29) 
			return 0;		
		else if (backpack_weight < 35)
			return 0;
		else
			return 0;	
	}else if(avg_slope_procent<70){ //urcat pe trepte: 70% = unghi de 45 grade
		if (backpack_weight < 3)
			return 4;
        if (backpack_weight < 7) 
			return 5;
		else if (backpack_weight < 11) 
			return 6;
		else if (backpack_weight < 17) 
			return 7;		
		else if (backpack_weight < 23) 
			return 8;
		else if (backpack_weight < 29) 
			return 9;		
		else if (backpack_weight < 35)
			return 10;
		else
			return 12;
	}else{
        if (backpack_weight < 3) 
			return 0;
		else if (backpack_weight < 7)
			return 0;
		else if (backpack_weight < 11) 
			return 0;
		else if (backpack_weight < 17) 
			return 0;		
		else if (backpack_weight < 23) 
			return 0;
		else if (backpack_weight < 29) 
			return 0;		
		else if (backpack_weight < 35)
			return 0;
		else
			return 0;		
	}	
}

/**
     * Calculates the Harris Benedict RMR value for an entity. Based on above calculation for Com
     *
     * @param gender   Users gender.
     * @param weightKg Weight in Kg.
     * @param age      Age in years.
     * @param heightCm Height in CM.
     * @return Harris benedictRMR value.
	 * gender = "male" or "female"
     */
public static double harrisBenedictRmr(String gender, double weightKg, double age, double heightCm) {
	if(gender.equals("male"))
		return 66.4730f + (5.0033f * heightCm) + (13.7516f * weightKg) - (6.7550f * age);
	else if(gender.equals("female"))
		return 655.0955f + (1.8496f * heightCm) + (9.5634f * weightKg) - (4.6756f * age);
	return 0.;
}





//====================================== METODA 4 =====================================
//=====================================================================================

/**
Pandolf equation return the metabolic rate, which is how quickly you’re burning energy. This equation gives you a value in watts, but that’s easy to convert to 
other units like calories per hour.

The inputs into the equation are:

weightKg: your weight (kg)
packWeightKg: the weight of your pack (kg)
hikingSpeed: your hiking speed (m/s)
slope: the grade of any incline (%)
terrain_factor: a "terrain factor" that adjusts the results for different surfaces (for example, a paved road has a terrain factor of 1.0, 
but a gravel road is 1.2, since it takes more calories to walk on a soft or uneven surface)
*/ 
public static double PandolfMR (double weightKg, double packWeightKg, double hikingSpeed, double slope, double terrain_factor){
	return 1.5*weightKg + 2.*(weightKg + packWeightKg)*Math.pow(packWeightKg/weightKg, 2) + terrain_factor*(weightKg + packWeightKg)*(1.5*Math.pow(hikingSpeed, 2) + 0.35*hikingSpeed*slope);
}




//====================================== METODA 5 =====================================
//=====================================================================================

/*
activity = cross_country, up_hill_down, up_hill_only, down_hill_only

incline = 1 for slope 1% to 5%
incline = 5 for slope 5% to 10%
incline = 10 for slope > 10%
*/
public static double calcCC(double weight, double backpack_weight, double distance, double minutes, String activity, int incline) {
    double c = 0;
	double w = weight + backpack_weight;
    double met = -1;
    double speed = closestSpeed(((distance/minutes) * 60) * 0.621371192);
	//double speed = closestSpeed((distance/minutes) * 60); //for miles

    //debug("Speed: " + speed);
    if (speed == 2.5) {
        met = 4.18;
    } else if (speed == 3) {
        met = 4.88;
    } else if (speed == 3.5) {
        met = 6;
    } else if (speed == 4) {
        met = 6.97;
    }
    //debug("MET: " + met);
    if (activity.equals("cross_country")) {
        met = met;
        //debug("new MET: " + met);
		c = cckg(w, met, minutes);
    } else if (activity.equals("up_hill_down")) {
        double c1 = -1;
        double c2 = -1;
        double met1 = -1;
        double met2 = -1;
        double halfTime = minutes / 2;
        met1 = getUpHillMet(met, incline);
        met2 = getDownHillMet(met, incline);
        //debug("new MET1: " + met1);
        //debug("new MET2: " + met2);
        c1 = cckg(w, met1, halfTime);
        c2 = cckg(w, met2, halfTime);
        c = c1 + c2;
    } else if (activity.equals("up_hill_only")) {
        double c1 = -1;
        double met1 = -1;
        met1 = getUpHillMet(met, incline);
        //debug("new MET1: " + met1);
		c1 = cckg(w, met1, minutes);
        c = c1;
    } else if (activity.equals("down_hill_only")) {
        double c2 = -1;
        double met2 = -1;
        met2 = getDownHillMet(met, incline);
        //debug("new MET2: " + met2);
		c2 = cckg(w, met2, minutes);
        c = c2;
    }
    //debug("c: " + c);
    return c;
}

/*
incline = 1 for slope 1% to 5%
incline = 5 for slope 5% to 10%
incline = 10 for slope > 10%
*/
public static double getUpHillMet(double met, int incline) {
	switch(incline){
		case 1: return met * 1.5;
		case 5: return met * 1.8;
		case 10: return met * 2.3;
	}
	return 0.;	
}

/*
incline = 1 for slope 1% to 5%
incline = 5 for slope 5% to 10%
incline = 10 for slope > 10%
*/
public static double getDownHillMet(double met, int incline) {
	switch(incline){
		case 1: return met * 0.85;
		case 5: return met * 1.;
		case 10: return met * 1.14;
	}
	return 0.;
}

public static double closestSpeed(double num) {
    double[] knownSpeeds = {2.5, 3., 3.5, 4.};
    double curr = knownSpeeds[0];
    double diff = Math.abs(num - curr);
    for (int val = 0; val < knownSpeeds.length; val++) {
        double newdiff = Math.abs(num - knownSpeeds[val]);
        if (newdiff < diff) {
            diff = newdiff;
            curr = knownSpeeds[val];
        }
    }
    return curr;
}

public static double cckg(double kg, double met, double mins) {
    return met * 3.5 * kg / 200 * mins;
}

public static void debug(String msg) {
	System.out.println("Debug: " + msg);
}




//=====================================================================================
//=====================================================================================

/**
     * Gets a users age from a date.
     * @param age The date of birth.
     * @return The age in years.
*/
public static double getAgeFromDateOfBirth(Date age) {
	Calendar currentDate = Calendar.getInstance();
	Calendar dateOfBirth = Calendar.getInstance();
	dateOfBirth.setTime(age);
	if (dateOfBirth.after(currentDate)) throw new IllegalArgumentException("Can't be born in the future");
	int currentYear = currentDate.get(Calendar.YEAR);
	int dateOfBirthYear = dateOfBirth.get(Calendar.YEAR);
	int age2 = currentYear - dateOfBirthYear;
	int currentMonth = currentDate.get(Calendar.MONTH);
	int dateOfBirthMonth = dateOfBirth.get(Calendar.MONTH);
	if (dateOfBirthMonth > currentMonth) {
		age2--;
	} else if (currentMonth == dateOfBirthMonth) {
		int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
		int dateOfBirthDay = dateOfBirth.get(Calendar.DAY_OF_MONTH);
		if (dateOfBirthDay > currentDay) {
			age2--;
		}
	}	
	int nrziledinan = currentDate.get(Calendar.DAY_OF_YEAR);	
	int nrziledinannastere = dateOfBirth.get(Calendar.DAY_OF_YEAR);	
	int plusnrzile = nrziledinan > nrziledinannastere ? nrziledinan - nrziledinannastere : nrziledinan - nrziledinannastere + 365;
	return age2 + roundToDecimals(plusnrzile/365.,2);
}

public static double roundToDecimals(double d, int n) {
	int t1=(int)((d*Math.pow(10,n)));
	int t2=(int)((d*Math.pow(10,n+1)));
	double d1=(double)t1/Math.pow(10,n);
	double d2=(double)t2/Math.pow(10,n+1);
	long c = Math.round(Math.abs(d1-d2)*Math.pow(10,n+1));
	if (c>=5) d1+=Math.pow(10,-n);
	return d1;
}

}



//======================================================
/*
double pret_calorie = 0.005;
var alimente = new Array();

function calculPretCalorie(){
							//	[denumire,                        		pret, 			gramaj/buc, kcal/100g, 	kcal/buc, pret_kcal]
	alimente[0] 	= ["baton ciocolata Lion (Nestle)", 		1.8, 				63, 				473, 				0, 				0];
	alimente[1] 	= ["baton ciocolata Snickers Dark", 		2.15, 			51, 				490, 				0, 				0];
	alimente[2] 	= ["miere de albine", 									2, 					100, 				325, 				0, 				0];
	alimente[3] 	= ["polen", 														2.2, 				20, 				417, 				0, 				0];
	alimente[4] 	= ["ou de gaina", 											0.7, 				55, 				143, 				0, 				0];
	alimente[5] 	= ["ou de prepelia", 										0.29, 			10, 				158, 				0, 				0];
	alimente[6] 	= ["cascaval", 													2, 					100, 				283, 				0, 				0];
	alimente[7] 	= ["fulgi de porumb", 									1.5, 				100, 				360, 				0, 				0];
	alimente[8] 	= ["musly crunchy Stilla Natura", 			1, 					75, 				466, 				0, 				0];
	alimente[9] 	= ["baton cereale Nestle Fitness", 			1.61, 			23.5, 			372, 				0, 				0];
	alimente[10] 	= ["rosie", 														0.5, 				100, 				18, 				0, 				0];
	alimente[11] 	= ["banana", 														0.85, 			170, 				89, 				0, 				0];
	alimente[12] 	= ["mar", 															0.5, 				100, 				51, 				0, 				0];
	alimente[13] 	= ["portocala", 												1.4, 				200, 				47, 				0, 				0];
	alimente[14] 	= ["piersica", 													0.5, 				100, 				39, 				0, 				0];
	alimente[15] 	= ["alune", 														2.95, 			200, 				567, 				0, 				0];
	alimente[16] 	= ["nuci", 															3, 					100, 				654, 				0, 				0];
	alimente[17] 	= ["bere", 															2.5, 				500, 				43, 				0, 				0];
	alimente[17] 	= ["paine alba", 												0.115, 			25, 				266, 				0, 				0]; 			//o felie
	
		
	var schema1 = new Array();
	schema1[0] = 2;
	schema1[2] = 1;
	schema1[4] = 3;
	schema1[6] = 1;
	schema1[7] = 1;
	schema1[9] = 4;
	schema1[10] = 2;
	schema1[11] = 2;
	schema1[16] = 1;
	schema1[17] = 5;	
	for(var i = 0; i < alimente.length; i++) if(schema1[i] === undefined) schema1[i] = 0;
	
	
	for(var i = 0; i < alimente.length; i++){
		alimente[i][1] = parseFloat(alimente[i][1]);
		alimente[i][2] = parseFloat(alimente[i][2]);
		alimente[i][3] = parseFloat(alimente[i][3]);
		alimente[i][4] = parseFloat((alimente[i][2] * alimente[i][3] / 100).toFixed(2));
		alimente[i][5] = parseFloat((alimente[i][1] / alimente[i][4]).toFixed(5));
	}
	
	var kcal = 0;
	var pret = 0;
	var greutate = 0;
	for(var i = 0; i < alimente.length; i++){
		greutate += schema1[i] * alimente[i][2];
		kcal += schema1[i] * alimente[i][4];
		pret += schema1[i] * alimente[i][1];
	}
	
	pret_calorie = (pret / kcal).toFixed(4);
	//console.log("pret calorie: " + pret_calorie);
}
*/
//======================================================

	
	
