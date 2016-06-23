import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by robin on 4/5/16.
 */
public class TinyGPS {
    public class Location{
        public double latitude;
        public double longitude;
        private boolean updated=false;

        private Location(double latitude, double longitude){
            this.latitude=latitude;
            this.longitude=longitude;
        }
        public Location(){

        }
        public void set(Location location){
            this.latitude=location.latitude;
            this.longitude=location.longitude;
        }
        @Override
        public String toString() {
            return latitude+","+longitude;
        }
        public boolean isValid(){
            return (latitude!=0.0)&&(longitude!=0.0);
        }

        @Override
        protected Location clone() {
            return new Location(latitude,longitude);
        }
    }
    private Date date;
    private Date time;
    private double speed;
    private Location location;
    private Location lastKnownGoodLocation;
    private String quality;
    private int satellites;

    public Date getDate() {
        return date;
    }

    public Location getLocation() {
        return location;
    }

    public String getQuality() {
        return quality;
    }

    public int getSatellites() {
        return satellites;
    }

    public double getHorizontalDilution() {
        return horizontalDilution;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getGeoidalSep() {
        return geoidalSep;
    }

    public double getAge() {
        return age;
    }

    public int getStationId() {
        return stationId;
    }
    public Date getTime() {
        return time;
    }

    public double getSpeed() {
        return speed;
    }

    private double horizontalDilution;
    private double altitude;
    private double geoidalSep;
    private double age;
    private int stationId;

    public Location getLastKnownGoodLocation() {
        return lastKnownGoodLocation;
    }

    public TinyGPS() {
        location=new Location();
        lastKnownGoodLocation=new Location();
        date=new Date();
        try {
            System.out.println("Change it to ttyS0 in Raspberry pi 3");
            new Thread(new TinyGPSReader("/dev/ttyAMA0")).start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean encode(String sentence) {
        if(sentence.length()>6){
            switch (sentence.substring(1,6)){
                case "GPRMC":
                    return decodeGPRMC(sentence);
                case "GPGGA":
                    return decodeGPGGA(sentence);
                default:
                    break;
            }
        }
        return false;
    }

    private boolean decodeGPRMC(String sentence) {
        String[] terms=sentence.split(",");
        if(terms.length<12)
            return false;
        String timeString=terms[1];
        String dataStatusString=terms[2];
        String latitudeString=terms[3];
        String northString=terms[4];
        String longitudeString=terms[5];
        String eastString=terms[6];
        String speedString=terms[7];
        String trackModeString=terms[8];
        String dateString=terms[9];
        String magneticVaraitionString=terms[10];
        String magneticDirectionString=terms[11].split("\\*")[0];
        SimpleDateFormat formatter=new SimpleDateFormat("HHmmss.SS ddMMyy");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            this.date= formatter.parse(timeString+" "+dateString);
            onUpdate(date);
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        double temp;
        try {
            temp= Double.parseDouble(latitudeString);
        }catch(NumberFormatException e){
            temp=0.0;
        }
        this.location.latitude=((int)(temp/100))+(temp%100)/60;
        try {
            temp=Double.parseDouble(longitudeString);
        }catch(NumberFormatException e){
            temp=0.0;
        }
        this.location.longitude=((int)(temp/100))+(temp%100)/60;
        if(this.location.isValid()) {
            onUpdate(location);
        }
        try {
            speed= Double.parseDouble(speedString);
        }catch(NumberFormatException e){
            speed=0.0;
        }
        return true;
    }



    private boolean decodeGPGGA(String sentence) {
        String[] terms=sentence.split(",");
        if(terms.length<15)
            return false;
        String timeString=terms[1];
        String latitudeString=terms[2];
        String northString=terms[3];
        String longitudeString=terms[4];
        String eastString=terms[5];
        String qualityString=terms[6];
        String numSatellitesString=terms[7];
        String horizontalDilutionString=terms[8];
        String altitudeString=terms[9];
        String unitAltitudeString=terms[10];
        String geoidalSeparationString=terms[11];
        String geoidalSeparationUnitString=terms[12];
        String ageString=terms[13];
        String stationIdString=terms[14].split("\\*")[0];
        SimpleDateFormat formatter=new SimpleDateFormat("HHmmss.SS ddMMyy");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat dateFormat=new SimpleDateFormat("ddMMyy");
        String dateString=dateFormat.format(this.date);
        try {
            this.time= formatter.parse(timeString+" "+dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        double temp;
        try {
            temp= Double.parseDouble(latitudeString);
        }catch(NumberFormatException e){
            temp=0.0;
        }
        this.location.latitude=((int)(temp/100))+(temp%100)/60;
        try {
            temp=Double.parseDouble(longitudeString);
        }catch(NumberFormatException e){
            temp=0.0;
        }
        this.location.longitude=((int)(temp/100))+(temp%100)/60;
        if(this.location.isValid()) {
            this.location.updated = true;
            onUpdate(location);
        }
        if(qualityString=="1"){
            quality="GPS fix";
        }else if (qualityString=="0"){
            quality="No fix";
        } else if (qualityString == "2") {
            quality="Diff GPS fix";
        }else{
            quality="No quality";
        }
        try {
             satellites= Integer.parseInt(numSatellitesString);
        }catch(NumberFormatException e){
            satellites=0;
        }
        try {
            horizontalDilution= Double.parseDouble(horizontalDilutionString);
        }catch(NumberFormatException e){
            horizontalDilution=0.0;
        }
        try {
            altitude= Double.parseDouble(altitudeString);
        }catch(NumberFormatException e){
            altitude=0.0;
        }
        //String altitudeUnit=unitAltitudeString;
        try {
            geoidalSep= Double.parseDouble(geoidalSeparationString);
        }catch(NumberFormatException e){
            geoidalSep=0.0;
        }
        //String geoidalSeparationUnit=geoidalSeparationUnitString;
        double age;
        try {
            age= Double.parseDouble(ageString);
        }catch(NumberFormatException e){
            age=0.0;
        }
        try {
            stationId= Integer.parseInt(stationIdString);
        }catch(NumberFormatException e){
            stationId=0;
        }

        return true;
    }
    private class TinyGPSReader implements Runnable{
        private InputStream stream;
        private BufferedReader bufferedReader;
        public TinyGPSReader(InputStream stream){
            this.stream=stream;
            bufferedReader=new BufferedReader(new InputStreamReader(stream));
        }
        public TinyGPSReader(String fileName) throws FileNotFoundException {
            this(new FileInputStream(fileName));
        }
        @Override
        public void run() {
            String line;
            try {
                while((line=bufferedReader.readLine())!=null){
                    TinyGPS.this.encode(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public List<LocationUpdatedListener> locationUpdatedListeners=new ArrayList<LocationUpdatedListener>();
    private void onUpdate(Location location){
        lastKnownGoodLocation.set(location);
        for(LocationUpdatedListener locationUpdatedListener:locationUpdatedListeners){
            locationUpdatedListener.onupdate(this,location);
        }
    }
    public interface LocationUpdatedListener extends EventListener {
        void onupdate(TinyGPS tinyGps,Location location);
    }
    public void addLocationUpdatedListener(LocationUpdatedListener locationUpdatedListener){
        locationUpdatedListeners.add(locationUpdatedListener);
    }
    public void removeLocationUpdatedListener(LocationUpdatedListener locationUpdatedListener){
        locationUpdatedListeners.remove(locationUpdatedListener);
    }

    public List<DateUpdatedListener> dateUpdatedListeners=new ArrayList<DateUpdatedListener>();
    private void onUpdate(Date date){
        for (DateUpdatedListener dateUpdatedListener : dateUpdatedListeners) {
            dateUpdatedListener.onUpdate(this, this.date);
        }
    }
    public interface DateUpdatedListener extends EventListener{
        void onUpdate(TinyGPS tinyGps, Date date);
    }
    public void addDateUpdatedListener(DateUpdatedListener dateUpdatedListener){
        dateUpdatedListeners.add(dateUpdatedListener);
    }
    public void removeDateUpdatedListener(DateUpdatedListener dateUpdatedListener){
        dateUpdatedListeners.remove(dateUpdatedListener);
    }
}