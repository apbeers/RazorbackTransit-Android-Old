package razorbacktransit.arcu.razorbacktransit;

/**
 * Created by Andrew on 10/5/17.
 */

public class PDFFactory {
    
    private PDF BLUE22 = new PDF("Blue 22", "blue_22");
    private PDF BROWN17 = new PDF("Brown 17", "brown_17");
    private PDF DICKSONST = new PDF("Dickson St. 7", "dicksonst_07");
    private PDF GREEN11 = new PDF("Green 11", "green_11");
    private PDF ORANGE33 = new PDF("Orange 33", "orange_33");
    private PDF PURPLE44 = new PDF("Purple 44", "purple_44");
    private PDF RED26 = new PDF("Red 26", "red_26");
    private PDF REMOTEEXPRESS = new PDF("Remote Express 48", "remoteexpress_48");
    private PDF ROUTE13 = new PDF("Route 13", "route_13");
    private PDF TAN13 = new PDF("Tan 35", "tan_35");
    private PDF BLUEREDUCED = new PDF("Blue Reduced", "bluereduced_02");
    private PDF GREENREDUCED = new PDF("Green Reduced", "greenreduced_01");
    private PDF ORANGEREDUCED = new PDF("Orange Reduced", "orangereduced_03");
    private PDF PURPLEREDUCED = new PDF("Purple Reduced", "purplereduced_04");
    private PDF REDREDUCED = new PDF("Red Reduced", "redreduced_06");
    private PDF TANREDUCED = new PDF("Tan Reduced", "tanreduced_05");
    
    private PDF BLUE22_ROUTE = new PDF("Blue 22", "blue_22_route");
    private PDF BROWN17_ROUTE = new PDF("Brown 17", "brown_17_route");
    private PDF DICKSONST_ROUTE = new PDF("Dickson St. 7", "downtown_17_route");
    private PDF GREEN11_ROUTE = new PDF("Green 11", "green_11_route");
    private PDF ORANGE33_ROUTE = new PDF("Orange 33", "orange_33_route");
    private PDF PURPLE44_ROUTE = new PDF("Purple 44", "purple_44_route");
    private PDF RED26_ROUTE = new PDF("Red 26", "red_26_route");
    private PDF REMOTEEXPRESS_ROUTE = new PDF("Remote Express 48", "remoteexpress_48_route");
    private PDF ROUTE13_ROUTE = new PDF("Route 13", "route_13_route");
    private PDF TAN13_ROUTE = new PDF("Tan 35", "tan_35_route");
    private PDF BLUEREDUCED_ROUTE = new PDF("Blue Reduced", "bluereduced_02_route");
    private PDF GREENREDUCED_ROUTE = new PDF("Green Reduced", "greenreduced_01_route");
    private PDF ORANGEREDUCED_ROUTE = new PDF("Orange Reduced", "orangereduced_03_route");
    private PDF PURPLEREDUCED_ROUTE = new PDF("Purple Reduced", "purplereduced_04_route");
    private PDF REDREDUCED_ROUTE = new PDF("Red Reduced", "redreduced_06_route");
    private PDF TANREDUCED_ROUTE = new PDF("Tan Reduced", "tanreduced_05_route");
    public PDF[] getRegularSchedules() {

        PDF[] regularSchedules = {BLUE22, BROWN17, DICKSONST, GREEN11, ORANGE33, PURPLE44, RED26, REMOTEEXPRESS, ROUTE13, TAN13};
        return regularSchedules;
    }

    public PDF[] getReducedSchedules() {
        PDF[] reducedSchedules = {BLUEREDUCED, GREENREDUCED, ORANGEREDUCED, PURPLEREDUCED, REDREDUCED, TANREDUCED};
        return reducedSchedules;
    }

    public PDF[] getAllSChedules() {

        PDF[] allSchedules = {BLUE22, BROWN17, DICKSONST, GREEN11, ORANGE33, PURPLE44, RED26, REMOTEEXPRESS, ROUTE13, TAN13, BLUEREDUCED, GREENREDUCED, ORANGEREDUCED, PURPLEREDUCED, REDREDUCED, TANREDUCED};
        return  allSchedules;
    }

    public PDF[] getAllRoutes() {

        PDF[] allRoutes = {BLUE22_ROUTE, BROWN17_ROUTE, DICKSONST_ROUTE, GREEN11_ROUTE, ORANGE33_ROUTE, PURPLE44_ROUTE, RED26_ROUTE, REMOTEEXPRESS_ROUTE, ROUTE13_ROUTE, TAN13_ROUTE, BLUEREDUCED_ROUTE, GREENREDUCED_ROUTE, ORANGEREDUCED_ROUTE, PURPLEREDUCED_ROUTE, REDREDUCED_ROUTE, TANREDUCED_ROUTE};
        return  allRoutes;
    }
}
