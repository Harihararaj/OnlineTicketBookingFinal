package com.company;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

class Train {
    int trainNumber;
    String station;
    int noOfSeats;
    int noOfWaitingListSeats;
    int noOfSeatsFilled;
    int noOfWaitingSeatsFilled;
    String source;
    String dest;
    Train(int trainNumber, String station, int noOfSeats, int noOfWaitingListSeats, int noOfSeatsFilled, int noOfWaitingSeatsFilled, String source, String dest){
        this.trainNumber=trainNumber;
        this.station=station;
        this.noOfSeats=noOfSeats;
        this.noOfWaitingListSeats=noOfWaitingListSeats;
        this.noOfSeatsFilled=noOfSeatsFilled;
        this.noOfWaitingSeatsFilled=noOfWaitingSeatsFilled;
        this.source=source;
        this.dest=dest;
    }
}
class Passenger{
    int p_id;
    String name;
    Passenger(int p_id,String name){
        this.p_id=p_id;
        this.name=name;
    }
}
class Ticket{
    int pnr;
    String source;
    String dest;
    Ticket(int pnr,String source,String dest){
        this.pnr=pnr;
        this.source=source;
        this.dest=dest;
    }
}
class PassengerTicket{
    int pt_id;
    int p_id;
    int seatNumber;
    int pnr;
    int trainNumber;
    PassengerTicket(int pt_id,int p_id,int seatNumber,int pnr,int trainNumber){
        this.pt_id=pt_id;
        this.p_id=p_id;
        this.seatNumber=seatNumber;
        this.pnr=pnr;
        this.trainNumber=trainNumber;
    }
}
public class Main {

    static ArrayList<Train> train = new ArrayList<>();
    static ArrayList<Passenger> passenger = new ArrayList<>();
    static ArrayList<Ticket> ticket = new ArrayList<>();
    static ArrayList<PassengerTicket> passengerTicket = new ArrayList<>();
    static ArrayList<Integer> routeFromSourceToDestination = new ArrayList<>();

    static ArrayList<String> transitInRoute = new ArrayList<>();
    static int pnr = 10, p_id = 1, pt_id = 200;
    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IllegalStateException {

        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:reference8.db");
        Statement stm = conn.createStatement();

        createTable(stm);


        copyTableValues(stm);
        int pnr1=0,pt_idd=0,p_idd=0;
        for (PassengerTicket value : passengerTicket) {
            if (value.pnr > pnr1) {
                pnr1 = value.pnr;
            }
            if (value.p_id > p_idd) {
                p_idd = value.p_id;
            }
            if (value.pt_id > pt_idd) {
                pt_idd = value.pt_id;
            }
        }
        pnr=pnr1+10;
        pt_id=pt_idd+10;
        p_id=p_idd+10;
        if(pnr==10) {
            tableValues(stm);
            copyTableValues(stm);
        }
        pnr++;
        int ifYouHaveToContinue = 1;
        while (ifYouHaveToContinue == 1) {
            System.out.println("Book Ticket:1\nCancel Ticket:2\nOccupancy Chart:3\nView Tables:4\nDisplay:5\nExit:6");
            int operationToBeDone = scan.nextInt();
            switch (operationToBeDone) {
                case 1 -> {
                    booking(stm);
                    copyBack(stm, 0);
                    routeFromSourceToDestination.clear();
                    transitInRoute.clear();
                    PassengerIdOfTickets.clear();
                }
                case 2 -> {
                    deleteTicketsInSerialNumber();
                    copyBack(stm, 0);
                    routeFromSourceToDestination.clear();
                    transitInRoute.clear();
                    PassengerIdOfTickets.clear();
                }
                case 3 -> printOccupancy();
                case 4 -> copyBack(stm, 1);
                case 5 -> {
                    display();
                    routeFromSourceToDestination.clear();
                    transitInRoute.clear();
                    PassengerIdOfTickets.clear();
                }
                case 6 -> {
                }

                default -> throw new IllegalStateException("Unexpected value: " + operationToBeDone);
            }
            System.out.println("Enter 1 to continue");
            ifYouHaveToContinue = scan.nextInt();
        }
        System.out.println("Thanks for Using this App (:");
    }
    static void printOccupancy(){
        System.out.println("The Train numbers are :");
        for (Train value : train) {
            System.out.println("Train : " + value.trainNumber);
        }
        System.out.println("Enter the Train number to see the Occupancy chart :");
        int trainNumber=scan.nextInt();
        occupancy(trainNumber);
    }
    static void display(){
        System.out.println("Enter your pnr number to display the ticket");
        int pnr = scan.nextInt();
        String so=null,d=null;
        for(int i=0;i<ticket.size();i++){
            if(ticketHavePnr(i, pnr)){
                so=ticket.get(i).source;
                d=ticket.get(i).dest;
            }
        }
         findingRoute(so,d,-1);
        int sizeOfRouteList = routeFromSourceToDestination.size();
        for(int i = 0; i< sizeOfRouteList -1; i++){
            if(routeFromSourceToDestination.get(i).equals(routeFromSourceToDestination.get(i + 1))){
                routeFromSourceToDestination.remove(i+1);
                sizeOfRouteList--;
                i--;
            }
        }
        displayTicketUsingPnr(pnr);
        PassengerIdOfTickets.clear();
        routeFromSourceToDestination.clear();
        transitInRoute.clear();
    }
    static boolean ticketHavePnr(int i,int m){
        return ticket.get(i).pnr==m;
    }
    static void booking(Statement stm) throws SQLException {
        System.out.println("Enter source");
        String source=scan.next();
        System.out.println("Enter destination");
        String dest=scan.next();
        if(findingRoute(source,dest,-1)==1) {
            deletingDuplicateRoute();
            transitInRoute.add(dest);
            transitInRoute.add(0,source);
            if(bookTicketsForTheRoutes(source,dest)==1){
                copyBack(stm, 0);
                System.out.println();
                System.out.println("Route to destination");
                printRouteDetails();

                routeFromSourceToDestination.clear();
                transitInRoute.clear();
            }
            else{
                System.out.println("Your ticket is not booked due to no vacancy");
            }

        }
        else {
            System.out.println("Your ticket is not Booked since you have not entered a Valid Source and Destination");
        }
        copyBack(stm,0);
    }
    static void deletingDuplicateRoute(){
        int v= routeFromSourceToDestination.size();
        for(int i=0;i<v-1;i++){
            if(routeFromSourceToDestination.get(i).equals(routeFromSourceToDestination.get(i + 1))){
                routeFromSourceToDestination.remove(i+1);
                v--;
                i--;
            }
        }
    }
    static void printRouteDetails(){
        for(int i = 0; i< routeFromSourceToDestination.size(); i++){
            System.out.print(routeFromSourceToDestination.get(i)+"====");
            System.out.println(transitInRoute.get(i)+"->"+ transitInRoute.get(i+1));
        }
    }
    static void deleteTicketsInSerialNumber() {
        System.out.println("Enter the pnr number to delete the ticket");

        int pnrToDelete = scan.nextInt();
        int flag = 0;
        for (Ticket value : ticket) {
            if (pnrToDelete == value.pnr) {
                flag = 1;
                break;
            }
        }
        if (flag == 0)
        {
            System.out.println("Enter PNR is not present please specify the valid pnr");
        }
        else
        {
            String source =null, destination =null;
            for (Ticket item : ticket) {
                if (item.pnr == pnrToDelete) {
                    source = item.source;
                    destination = item.dest;
                }
            }
            findingRoute(source, destination,-1);
            int v= routeFromSourceToDestination.size();
            System.out.println();
            for(int i=0;i<v-1;i++){
                if(routeFromSourceToDestination.get(i).equals(routeFromSourceToDestination.get(i + 1))){
                    routeFromSourceToDestination.remove(i+1);
                    v--;
                    i--;
                }
            }

            displayTicketUsingPnr(pnrToDelete);
            routeFromSourceToDestination.clear();
            transitInRoute.clear();
            System.out.println("If you want to delete all tickets in this PNR press 1 or If you want to delete the particular serial press 2:");
            int c = scan.nextInt();
            if (c == 1) {
                delete(pnrToDelete);
                for(int i=0;i<ticket.size();i++){
                    if(ticket.get(i).pnr== pnrToDelete){
                        ticket.remove(i);
                        break;
                    }
                }
            }
            else
            {
                System.out.println("Enter the Serial number");
                int s = scan.nextInt();
                int ptt_id = PassengerIdOfTickets.get(s - 1);
                PassengerIdOfTickets.clear();
                int pnr1=0;
                int p_id = 0;
                for (PassengerTicket value : passengerTicket) {
                    if (value.pt_id == ptt_id) {
                        p_id = value.p_id;
                    }
                }
                int si=passengerTicket.size();
                for(int i=0;i<si;i++){
                    if(passengerTicket.get(i).p_id==p_id) {
                        pnr1=passengerTicket.get(i).pnr;
                        deleteUsingTicketId(passengerTicket.get(i).pt_id);
                        si = passengerTicket.size();
                        i--;
                    }
                }
                int counter=0;
                for (PassengerTicket value : passengerTicket) {
                    if (value.pnr == pnr1) {
                        counter = 1;
                        break;
                    }
                }
                if(counter==0){
                    for(int i=0;i<ticket.size();i++){
                        if(ticket.get(i).pnr==pnr1){
                            ticket.remove(i);
                            break;
                        }
                    }
                }

            }
        }

    }
    static void deleteUsingTicketId(int ticketId){

        for(int i=0;i<passengerTicket.size();i++){
            if(passengerTicket.get(i).pt_id==ticketId){
                deletePassengerFromItsTable(i);
                findingTrainAndUpdatingTheNumberOfSeats(i);
                passengerTicket.remove(i);
                break;
            }
        }
    }
    static void deletePassengerFromItsTable(int i){
        for(int j=0;j<passenger.size();j++){
            if (passenger.get(j).p_id==passengerTicket.get(i).p_id){
                System.out.println(passenger.get(j).name+" is deleted");
                passenger.remove(j);
                break;
            }
        }
    }
    static void findingTrainAndUpdatingTheNumberOfSeats(int i){
        for(int j=0;j<train.size();j++){
            if (train.get(j).trainNumber==passengerTicket.get(i).trainNumber){
                if(whetherWaitingListSeat(i)){
                    deletingWaitingListSeats(j);
                }
                else{
                    deletingSeatFromTrain(j,i);
                }
            }
        }
    }
    static void deletingSeatFromTrain(int j,int i){
        Train t=new Train(train.get(j).trainNumber,train.get(j).station,train.get(j).noOfSeats,train.get(j).noOfWaitingListSeats,(train.get(j).noOfSeatsFilled-1),train.get(j).noOfWaitingSeatsFilled,train.get(j).source,train.get(j).dest);
        train.set(j,t);
        movingWaitingListSeats(i);
    }
    static void movingWaitingListSeats(int i){
        int seatNumber =passengerTicket.get(i).seatNumber;
        for(int k=0;k<passengerTicket.size();k++){
            if (passengerTicket.get(k).seatNumber==0 && passengerTicket.get(k).trainNumber==passengerTicket.get(i).trainNumber){
                PassengerTicket p=new PassengerTicket(passengerTicket.get(k).pt_id,passengerTicket.get(k).p_id, seatNumber,passengerTicket.get(k).pnr,passengerTicket.get(k).trainNumber);
                passengerTicket.set(k,p);
                updatingMovedWaitingListSeatToFilledSeat(k);
                break;
            }
        }
    }
    static void updatingMovedWaitingListSeatToFilledSeat(int k){
        for(int l=0;l<train.size();l++){
            if(train.get(l).trainNumber==passengerTicket.get(k).trainNumber){
                Train t=new Train(train.get(l).trainNumber,train.get(l).station,train.get(l).noOfSeats,train.get(l).noOfWaitingListSeats,(train.get(l).noOfSeatsFilled+1),(train.get(l).noOfWaitingSeatsFilled-1),train.get(l).source,train.get(l).dest);
                train.set(l,t);
            }
        }
    }
    static boolean whetherWaitingListSeat(int i){
        return passengerTicket.get(i).seatNumber==0;
    }
    static void deletingWaitingListSeats(int j){
        Train t=new Train(train.get(j).trainNumber,train.get(j).station,train.get(j).noOfSeats,train.get(j).noOfWaitingListSeats,train.get(j).noOfSeatsFilled,(train.get(j).noOfWaitingSeatsFilled-1),train.get(j).source,train.get(j).dest);
        train.set(j,t);
    }
    static void delete(int pnr){
        int sizeOfTicketTable =passengerTicket.size();
        int b=0;
        for(int i = 0; i< sizeOfTicketTable -b; i++){

            if(passengerTicket.get(i).pnr==pnr){


                deletingPassenger(i);
                deletingFromTrainAndUpdatingWaitingListSeats(i);
                passengerTicket.remove(i);
                b++;
                i--;

            }
        }
        sizeOfTicketTable =ticket.size();
        b=0;
        for(int i = 0; i< sizeOfTicketTable -b; i++){
            if(ticket.get(i).pnr==pnr){
                ticket.remove(i);
                i--;
                b++;
            }
        }
    }
    static void deletingFromTrainAndUpdatingWaitingListSeats(int i){
        for(int j=0;j<train.size();j++){
            if(passengerTicket.get(i).trainNumber==train.get(j).trainNumber){
                if(passengerTicket.get(i).seatNumber==0){
                    Train t=new Train(train.get(j).trainNumber,train.get(j).station,train.get(j).noOfSeats,train.get(j).noOfWaitingListSeats,train.get(j).noOfSeatsFilled,(train.get(j).noOfWaitingSeatsFilled-1),train.get(j).source,train.get(j).dest);
                    train.set(j,t);
                }
                else{
                    int sno=passengerTicket.get(i).seatNumber;
                    Train t=new Train(train.get(j).trainNumber,train.get(j).station,train.get(j).noOfSeats,train.get(j).noOfWaitingListSeats,(train.get(j).noOfSeatsFilled-1),train.get(j).noOfWaitingSeatsFilled,train.get(j).source,train.get(j).dest);
                    train.set(j,t);
                    findingWaitingListAndUpdating(i,sno);
                }
                break;
            }
        }
    }
    static void findingWaitingListAndUpdating(int i,int sno){
        for(int k=0;k<passengerTicket.size();k++){
            if(passengerTicket.get(k).trainNumber==passengerTicket.get(i).trainNumber && passengerTicket.get(k).seatNumber==0){
                PassengerTicket p=new PassengerTicket(passengerTicket.get(k).pt_id,passengerTicket.get(k).p_id,sno,passengerTicket.get(k).pnr,passengerTicket.get(k).trainNumber);
                passengerTicket.set(k,p);
                updatingDeletedToTrain(k);
                break;
            }
        }
    }
    static void updatingDeletedToTrain(int k){
        for(int z=0;z<train.size();z++){
            if(train.get(z).trainNumber==passengerTicket.get(k).trainNumber){
                Train t=new Train(train.get(z).trainNumber,train.get(z).station,train.get(z).noOfSeats,train.get(z).noOfWaitingListSeats,(train.get(z).noOfSeatsFilled+1),(train.get(z).noOfWaitingSeatsFilled-1),train.get(z).source,train.get(z).dest);
                train.set(z,t);

            }
        }
    }
    static void deletingPassenger(int i){
        for(int j=0;j<passenger.size();j++){
            if(passenger.get(j).p_id==passengerTicket.get(i).p_id){
                System.out.println(passenger.get(j).name+" is deleted");
                passenger.remove(j);
                break;
            }
        }
    }
    static int bookTicketsForTheRoutes(String source, String dest){
        System.out.println("Enter Number of Tickets ");
        int numberOfTicketsToBeBooked =scan.nextInt();
        int count=0;
        for (Integer integer : routeFromSourceToDestination) {
            for (Train value : train) {
                if (value.trainNumber == integer) {
                    int remain = ((value.noOfSeats + value.noOfWaitingListSeats) - (value.noOfSeatsFilled + value.noOfWaitingSeatsFilled));

                    if (numberOfTicketsToBeBooked <= remain) {
                        count++;
                    }
                }
            }
        }

        if(count== routeFromSourceToDestination.size()){
            pnr++;
            Ticket t=new Ticket(pnr,source,dest);
            ticket.add(t);

            while(numberOfTicketsToBeBooked !=0){
                System.out.println("Enter the Passenger name");
                String name=scan.next();
                p_id++;
                Passenger p=new Passenger(p_id,name);
                passenger.add(p);
                bookingTicket();
                numberOfTicketsToBeBooked--;
            }
            System.out.println("Your Ticket is Successfully Booked");
            displayTicketUsingPnr(pnr);
            return 1;
        }
        else{
            return 0;
        }
    }
    static void bookingTicket(){
        for (Integer integer : routeFromSourceToDestination) {
            for (int j = 0; j < train.size(); j++) {
                if (train.get(j).trainNumber == integer) {
                    if (train.get(j).noOfSeatsFilled < train.get(j).noOfSeats) {
                        int seat;
                        seat = findSeatNo(j,
                                train.get(j).trainNumber);
                        pt_id++;
                        PassengerTicket p1 = new PassengerTicket(pt_id, p_id, seat, pnr, train.get(j).trainNumber);
                        passengerTicket.add(p1);
                        train.get(j).noOfSeatsFilled = train.get(j).noOfSeatsFilled + 1;
                    } else {
                        pt_id++;
                        PassengerTicket p1 = new PassengerTicket(pt_id, p_id, 0, pnr, train.get(j).trainNumber);
                        passengerTicket.add(p1);
                        train.get(j).noOfWaitingSeatsFilled = train.get(j).noOfWaitingSeatsFilled + 1;
                    }
                }
            }
        }
    }
    static int findingRoute(String source, String dest, int f) {
        for (int i = 0; i < train.size(); i++) {
            if(i!=f) {
                String s1 = train.get(i).station;
                if(s1.contains(source) && s1.contains(dest)){
                    routeFromSourceToDestination.add(train.get(i).trainNumber);
                    return 1;
                }
                else if(s1.contains(source)) {
                    s1 = s1.substring(1, s1.length() - 1);
                    routeFromSourceToDestination.add(train.get(i).trainNumber);

                    for (int j = 0; j < train.size(); j++) {
                        if (j != i && j!=f) {


                            String s2 = train.get(j).station;
                            s2 = s2.substring(1, s2.length() - 1);
                            String[] str1 = s1.split(",");
                            String[] str2 = s2.split(",");
                            if(commonBetweenTwoStationExist(str1,str2)==1 && doesStringInStringList(str2,dest)==1){
                                routeFromSourceToDestination.add(train.get(j).trainNumber);
                                transitInRoute.add(stringCommonBetweenTwoStringList(str2,str1));
                                return 1;
                            }
                            else if(commonBetweenTwoStationExist(str1,str2)==1){
                                transitInRoute.add(stringCommonBetweenTwoStringList(str2,str1));
                                routeFromSourceToDestination.add(train.get(j).trainNumber);
                                return findingRoute(stringCommonBetweenTwoStringList(str2,str1),dest,i);

                            }

                        }
                    }
                }
            }
        }
        return 0;
    }

    static int commonBetweenTwoStationExist(String[] str1, String[] str2){


        for (String s : str1) {
            for (String value : str2) {
                if (s.equals(value)) {
                    return 1;
                }
            }
        }
        return 0;
    }
    static int doesStringInStringList(String[] str1, String s){
        for (String value : str1) {
            if (value.equals(s)) {
                return 1;
            }
        }
        return 0;
    }
    static String stringCommonBetweenTwoStringList(String[] str1, String[] str2){
        for (String s : str1) {
            for (String value : str2) {
                if (s.equals(value)) {
                    return s;
                }
            }
        }
        return null;
    }
    static Integer findSeatNo(int j,int t){
        for(int i=1;i<=train.get(j).noOfSeats;i++){
            int flag=0;
            for (PassengerTicket value : passengerTicket) {
                if (value.trainNumber == t) {
                    if (value.seatNumber == i) {
                        flag = 1;
                    }
                }
            }
            if(flag==0){
                return i;
            }
        }
        return null;
    }
    static void createTable(Statement stm) throws SQLException {
        stm.executeUpdate("CREATE TABLE IF NOT EXISTS passenger(p_id INT PRIMARY KEY,name VARCHAR(20))");
        stm.executeUpdate("CREATE TABLE IF NOT EXISTS ticket( pnr INT PRIMARY KEY,source VARCHAR(20),dest VARCHAR(20) )");
        stm.executeUpdate("CREATE TABLE IF NOT EXISTS passengerTicket( pt_id INT PRIMARY KEY,p_id INT ,seatNumber INT,pnr INT,trainNumber INT )");
        stm.executeUpdate("CREATE TABLE IF NOT EXISTS train(trainNumber INT PRIMARY KEY,station VARCHAR,noOfSeats INT,noOfWaitingListSeats INT,noOfSeatsFilled INT,noOfWaitingListSeatsFilled INT,source VARCHAR,dest VARCHAR)");
    }
    static void insertingIntoTrainTable(Statement stm,int trainNumber,String station,String source,String dest) throws SQLException {
        stm.executeUpdate("INSERT INTO train VALUES("+trainNumber+",'"+station+"',8,2,0,0,'"+source+"','"+dest+"')");
    }
    static void tableValues(Statement stm) throws SQLException {

        insertingIntoTrainTable(stm,1,"(A,B,C,D,E)","A","E");
        insertingIntoTrainTable(stm,2,"(X,Y,C)","X","C");
        insertingIntoTrainTable(stm,3,"(W,R,X)","W","X");
        insertingIntoTrainTable(stm,4,"(Q,Z,W)","Q","W");
        insertingIntoTrainTable(stm,5,"(N,M,Q)","N","Q");
        insertingIntoTrainTable(stm,6,"(F,P,N)","F","N");
        insertingIntoTrainTable(stm,7,"(K,S,F)","K","F");
        insertingIntoTrainTable(stm,8,"(T,U,K)","T","K");
        insertingIntoTrainTable(stm,9,"(V,I,T)","V","T");
        insertingIntoTrainTable(stm,10,"(G,H,V)","G","V");
    }
    static void copyTableValues(Statement stm) throws SQLException {
        ResultSet rs;
        rs=stm.executeQuery("SELECT * FROM train");
        while(rs.next())
        {
            Train t=new Train( rs.getInt("trainNumber"),rs.getString("station"),rs.getInt("noOfSeats"),rs.getInt("noOfWaitingListSeats"),rs.getInt("noOfSeatsFilled"),rs.getInt("noOfWaitingListSeatsFilled"),rs.getString("source"),rs.getString("dest"));
            train.add(t);
        }
        rs=stm.executeQuery("SELECT * FROM passenger");
        while(rs.next()){
            Passenger p=new Passenger(rs.getInt("p_id"),rs.getString("name"));
            passenger.add(p);
        }
        rs=stm.executeQuery("SELECT * FROM ticket");
        while(rs.next()){
            Ticket t=new Ticket(rs.getInt("pnr"),rs.getString("source"),rs.getString("dest"));
            ticket.add(t);
        }
        rs=stm.executeQuery("SELECT * FROM passengerTicket");
        while(rs.next()){
            PassengerTicket p=new PassengerTicket(rs.getInt("pt_id"),rs.getInt("p_id"),rs.getInt("seatNumber"),rs.getInt("pnr"),rs.getInt("trainNumber"));
            passengerTicket.add(p);
        }

    }
    static void copyBack(Statement stm, int toPrintIfNeeded) throws SQLException {
        stm.executeUpdate("DELETE FROM train");
        stm.executeUpdate("DELETE FROM passenger");
        stm.executeUpdate("DELETE FROM passengerTicket");
        stm.executeUpdate("DELETE FROM ticket");
        for (Train value : train) {
            stm.executeUpdate("INSERT INTO train VALUES(" + value.trainNumber + ",'" + value.station + "'," + value.noOfSeats + "," + value.noOfWaitingListSeats + "," + value.noOfSeatsFilled + "," + value.noOfWaitingSeatsFilled + ",'" + value.source + "','" + value.dest + "')");
        }
        for (Passenger value : passenger) {
            stm.executeUpdate("INSERT INTO passenger VALUES(" + value.p_id + ",'" + value.name + "')");
        }
        for (PassengerTicket value : passengerTicket) {
            stm.executeUpdate("INSERT INTO passengerTicket VALUES(" + value.pt_id + "," + value.p_id + "," + value.seatNumber + "," + value.pnr + "," + value.trainNumber + ")");
        }
        for (Ticket value : ticket) {
            stm.executeUpdate("INSERT INTO ticket VALUES(" + value.pnr + ",'" + value.source + "','" + value.dest + "')");
        }
        if(toPrintIfNeeded==1) {
            ResultSet rs;
            rs=stm.executeQuery("SELECT * FROM train");
            while(rs.next())
            {
                System.out.println(rs.getInt("trainNumber")+" "+rs.getString("station")+" "+rs.getInt("noOfSeats")+" "+rs.getInt("noOfWaitingListSeats")+" "+rs.getInt("noOfSeatsFilled")+" "+rs.getInt("noOfWaitingListSeatsFilled")+" "+rs.getString("source")+" "+rs.getString("dest"));

            }
            rs=stm.executeQuery("SELECT * FROM passenger");
            while(rs.next()){
                System.out.println(rs.getInt("p_id")+" "+rs.getString("name"));

            }
            rs=stm.executeQuery("SELECT * FROM ticket");
            while(rs.next()){
                System.out.println(rs.getInt("pnr")+" "+rs.getString("source")+" "+rs.getString("dest"));

            }
            rs=stm.executeQuery("SELECT * FROM passengerTicket");
            while(rs.next()){
                System.out.println(rs.getInt("pt_id")+" "+rs.getInt("p_id")+" "+rs.getInt("seatNumber")+" "+rs.getInt("pnr")+" "+rs.getInt("trainNumber"));

            }
        }
    }
    static void occupancy(int trainNumber){
        int flag=0;
        for(int i=0;i<train.size();i++){
            if (doesTrainHaveSeats(i,trainNumber)) {
                System.out.println("Train Number:" + train.get(i).trainNumber);
                gettingSeatAndPassengerName(i);
                System.out.println();
                flag=1;
            }

        }
        if(flag==0){
            System.out.println("No Booking Yet");
        }
    }
    static void gettingSeatAndPassengerName(int i){
        for (int j = 0; j < passengerTicket.size(); j++) {
            if (gettingTicketsWithAboveTrainNumber(i,j)) {
                printingPassengerNames(j);
            }
        }
    }
    static boolean gettingTicketsWithAboveTrainNumber(int i,int j){
        return passengerTicket.get(j).trainNumber == train.get(i).trainNumber;
    }
    static void printingPassengerNames(int j){
        for (Passenger value : passenger) {
            if (passengerTicket.get(j).p_id == value.p_id) {
                if (isPassengerWaiting(j)) {
                    System.out.println("Name:" + value.name + "\t" + "Seat number: Waiting List");
                } else {
                    System.out.println("Name:" + value.name + "\t" + "Seat number:" + passengerTicket.get(j).seatNumber);
                }
            }
        }
    }
    static boolean isPassengerWaiting(int j){
        return passengerTicket.get(j).seatNumber == 0;
    }
    static boolean doesTrainHaveSeats(int i,int trainNumber){
        return train.get(i).noOfSeatsFilled!=0 && train.get(i).trainNumber==trainNumber;
    }
    static ArrayList<Integer> PassengerIdOfTickets =new ArrayList<>();
    static void displayTicketUsingPnr(int originalPnr){


        int serial=1;
        for (Ticket value : ticket) {
            if (value.pnr == originalPnr) {
                System.out.println("PNR:" + value.pnr);
                System.out.println("Source:" + value.source);
                System.out.println("Destination:" + value.dest);
                serial = findingTrainsInRouteFromSourceToDestination(originalPnr, serial);
            }
        }
    }
    static int findingTrainsInRouteFromSourceToDestination(int originalPnr,int serial){
        for(int j = 0; j< routeFromSourceToDestination.size(); j++){
            System.out.println("Train Number:"+ routeFromSourceToDestination.get(j));
            serial=findingTicketInTheRoute(serial,originalPnr,j);
        }
        return serial;
    }
    static int findingTicketInTheRoute(int serial,int originalPnr,int j){
        for(int k=0;k<passengerTicket.size();k++){
            if(trainWithSamePnrAndHaveSeatNumber(k,originalPnr,j)){
                PassengerIdOfTickets.add(passengerTicket.get(k).pt_id);
                serial=printingPassengerWithSeatNumber(serial,k);
            }
            else if(trainWithSamePnrButWaiting(k,originalPnr,j)){
                PassengerIdOfTickets.add(passengerTicket.get(k).pt_id);
                serial=printingPassengerWithWaitingList(serial,k);
            }
        }
        return serial;
    }
    static boolean trainWithSamePnrAndHaveSeatNumber(int k,int originalPnr,int j) {
        return trainInRoute(k,originalPnr,j) && passengerTicket.get(k).seatNumber != 0;
    }
    static boolean trainWithSamePnrButWaiting(int k,int originalPnr,int j){
        return trainInRoute(k,originalPnr,j) && passengerTicket.get(k).seatNumber==0;
    }
    static boolean trainInRoute(int k,int originalPnr,int j){
        return passengerTicket.get(k).trainNumber == routeFromSourceToDestination.get(j) && passengerTicket.get(k).pnr == originalPnr;
    }
    static int printingPassengerWithSeatNumber(int serial,int k){
        for (Passenger value : passenger) {
            if (value.p_id == passengerTicket.get(k).p_id) {
                System.out.println(serial + ".Passenger Name:" + value.name + "\tSeat Number:" + passengerTicket.get(k).seatNumber);
                serial++;

            }
        }
        return serial;
    }
    static int printingPassengerWithWaitingList(int serial,int k){
        for (Passenger value : passenger) {
            if (value.p_id == passengerTicket.get(k).p_id) {
                System.out.println(serial + ".Passenger Name:" + value.name + "\tSeat Number: Waiting List");
                serial++;
            }
        }
        return serial;
    }

}

