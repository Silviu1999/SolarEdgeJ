package WriteREad;

public class Main {
    public static void main(String[] args) {
        Thread threadInfoQuarter=new Infoquarter();
        Thread threadInfoDay=new WriteREad.InfoDay();
        Thread threadPower=new Power();
        threadInfoQuarter.start();
        threadInfoDay.start();
        threadPower.start();



    }


}
