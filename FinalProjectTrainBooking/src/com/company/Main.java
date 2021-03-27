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
    int p_id;//Primary key
    String name;
    Passenger(int p_id,String name){
        this.p_id=p_id;
        this.name=name;
    }
}
class Ticket{
    int pnr;//Primary Key
    String source;
    String dest;
    Ticket(int pnr,String source,String dest){
        this.pnr=pnr;
        this.source=source;
        this.dest=dest;
    }
}
class PassengerTicket{
    int pt_id;//pri
    int p_id;//FK
    int seatNumber;
    int pnr;//Foreign key;
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

    static ArrayList<Train> train = new ArrayList<Train>();
    static ArrayList<Passenger> passenger = new ArrayList<Passenger>();
    static ArrayList<Ticket> ticket = new ArrayList<Ticket>();
    //  static ArrayList<Integer> a=new ArrayList<Integer>();
    static ArrayList<PassengerTicket> passengerTicket = new ArrayList<PassengerTicket>();
    static ArrayList<Integer> b=new ArrayList<Integer>();

    static ArrayList<String> transiter=new ArrayList<String>();
    static int pnr = 101, p_id = 1, pt_id = 200;
    static int tn1, tn2, tn3;
    static String transit;











    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // write your code here
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:reference74.db");
        Statement stm = conn.createStatement();
        ResultSet rs = null;

        createTable(stm);

        copyTableValues(stm);
        //copyback(stm,1);
        int pnr1=0,pt_idd=0,p_idd=0;
        for (int q = 0; q < passengerTicket.size(); q++) {
            if(passengerTicket.get(q).pnr>pnr1){
                pnr1=passengerTicket.get(q).pnr;
            }
            if(passengerTicket.get(q).p_id>p_idd){
                p_idd=passengerTicket.get(q).p_id;
            }
            if(passengerTicket.get(q).pt_id>pt_idd){
                pt_idd=passengerTicket.get(q).pt_id;
            }
        }
        pnr=pnr1+10;
        pt_id=pt_idd+10;
        p_id=p_idd+10;
        int x = 1;
        while (x == 1) {
            System.out.println("Book Ticket:1\nCancel Ticket:2\nOccupancy Chart:3\nView Tables:4\nDisplay:5\nExit:6");
            int n = scan.nextInt();
            if (n == 1) {
                System.out.println("Enter source");
                String source=scan.next();
                System.out.println("Enter destination");
                String dest=scan.next();
                if(rec(source,dest,-1)==1) {
                    int c=0;
                    int v=b.size();
                    for(int i=0;i<v-1;i++){
                        if(b.get(i)==b.get(i+1)){
                            b.remove(i+1);
                            v--;
                            i--;
                        }
                    }
//                    for(int i=0;i<b.size();i++){
//                        System.out.println(b.get(i));
//                    }

                    transiter.add(dest);
                    transiter.add(0,source);

                    if(book(source,dest)==1){

                        copyback(stm, 0);
                        System.out.println();
                        System.out.println("Route to destination");
                        for(int i=0;i<b.size();i++){
                            System.out.print(b.get(i)+"====");
                            System.out.println(transiter.get(i)+"->"+transiter.get(i+1));
                        }

                        b.clear();
                        transiter.clear();
                    }
                    else{
                        System.out.println("Your ticket is not booked due to no vacancy");
                    }

                }
                else{
                    System.out.println("Your ticket is not Booked since you have not entered a Valid Source and Destination");
                }
            } else if (n == 2) {
                deleteserial();
                copyback(stm, 0);
            } else if (n == 3) {
                occupancy();
            } else if (n == 4) {
                copyback(stm, 1);
            } else if (n == 5) {
                System.out.println("Enter your pnr number to display the ticket");
                int m = scan.nextInt();
                String so=null,d=null;
                for(int i=0;i<ticket.size();i++){
                    if(ticket.get(i).pnr==m){
                        so=ticket.get(i).source;
                        d=ticket.get(i).dest;
                    }
                }
                int z=rec(so,d,-1);
                int v=b.size();
                for(int i=0;i<v-1;i++){
                    if(b.get(i)==b.get(i+1)){
                        b.remove(i+1);
                        v--;
                        i--;
                    }
                }
                ArrayList a = displayy(m + 1);
                b.clear();
                transiter.clear();

            }
            else {
                break;
            }
            System.out.println("Enter 1 to continue");
            x = scan.nextInt();
        }
        System.out.println("Thanks for Using this App (:");
    }

    static void deleteserial() {
        System.out.println("Enter the pnr number to delete the ticket");

        int m = scan.nextInt();
        int flag = 0;
        for (int i = 0; i < ticket.size(); i++) {
            if (m == ticket.get(i).pnr) {
                flag = 1;
            }
        }
        if (flag == 0)
        {
            System.out.println("Enter PNR is not present please specify the valid pnr");
        }
        else
        {
            String so=null,d=null;
            for(int i=0;i<ticket.size();i++){
                if(ticket.get(i).pnr==m){
                    so=ticket.get(i).source;
                    d=ticket.get(i).dest;
                }
            }
            int z=rec(so,d,-1);
            int v=b.size();
            for(int i=0;i<v-1;i++){
                if(b.get(i)==b.get(i+1)){
                    b.remove(i+1);
                    v--;
                    i--;
                }
            }
            ArrayList a = displayy(m + 1);
            b.clear();
            transiter.clear();
            System.out.println("If you want to delete all tickets in this PNR press 1 or If you want to delete the particular serial press 2:");
            int c = scan.nextInt();
            if (c == 1) {
                delete(m);
                int counter=0;
                if(counter==0){
                    for(int i=0;i<ticket.size();i++){
                        if(ticket.get(i).pnr==m){
                            ticket.remove(i);
                            break;
                        }
                    }
                }
                System.out.println("All the seats in the particular pnr is deleted");
            }
            else
            {
                System.out.println("Enter the Serial number");
                int s = scan.nextInt();
                int count = 0;
                int ptt_id = (int) a.get(s - 1);
                int ptid=ptt_id;
                int pnrr=0;
                int p_id = 0;
                for(int i=0;i<passengerTicket.size();i++){
                    if(passengerTicket.get(i).pt_id==ptid){
                        p_id=passengerTicket.get(i).p_id;
                    }
                }
                int si=passengerTicket.size();
                for(int i=0;i<si;i++){
                    if(passengerTicket.get(i).p_id==p_id) {
                        deleteusingptid(passengerTicket.get(i).pt_id);
                        si = passengerTicket.size();
                        i--;
                    }
                }
                int counter=0;
                for(int i=0;i<passengerTicket.size();i++){
                    if(passengerTicket.get(i).pnr==pnrr){
                        counter=1;
                    }
                }
                if(counter==0){
                    for(int i=0;i<ticket.size();i++){
                        if(ticket.get(i).pnr==pnrr){
                            ticket.remove(i);
                            break;
                        }
                    }
                }
                System.out.println("The passenger in the specified Serial Number is deleted");

            }
        }

    }
    static void deleteusingptid(int ptid){

        for(int i=0;i<passengerTicket.size();i++){
            if(passengerTicket.get(i).pt_id==ptid){
                System.out.println(passengerTicket.get(i).pt_id);
                for(int j=0;j<passenger.size();j++){
                    if (passenger.get(j).p_id==passengerTicket.get(i).p_id){
                        passenger.remove(j);
                        //break;
                    }
                }
                for(int j=0;j<train.size();j++){
                    if (train.get(j).trainNumber==passengerTicket.get(i).trainNumber){
                        if(passengerTicket.get(i).seatNumber==0){
                            Train t=new Train(train.get(j).trainNumber,train.get(j).station,train.get(j).noOfSeats,train.get(j).noOfWaitingListSeats,train.get(j).noOfSeatsFilled,(train.get(j).noOfWaitingSeatsFilled-1),train.get(j).source,train.get(j).dest);
                            train.set(j,t);
                        }
                        else{
                            Train t=new Train(train.get(j).trainNumber,train.get(j).station,train.get(j).noOfSeats,train.get(j).noOfWaitingListSeats,(train.get(j).noOfSeatsFilled-1),train.get(j).noOfWaitingSeatsFilled,train.get(j).source,train.get(j).dest);
                            train.set(j,t);
                            int ssno=passengerTicket.get(i).seatNumber;
                            for(int k=0;k<passengerTicket.size();k++){
                                if (passengerTicket.get(k).seatNumber==0 && passengerTicket.get(k).trainNumber==passengerTicket.get(i).trainNumber){
                                    PassengerTicket p=new PassengerTicket(passengerTicket.get(k).pt_id,passengerTicket.get(k).p_id,ssno,passengerTicket.get(k).pnr,passengerTicket.get(k).trainNumber);
                                    passengerTicket.set(k,p);
                                    for(int l=0;l<train.size();l++){
                                        if(train.get(l).trainNumber==passengerTicket.get(k).trainNumber){
                                            t=new Train(train.get(l).trainNumber,train.get(l).station,train.get(l).noOfSeats,train.get(l).noOfWaitingListSeats,(train.get(l).noOfSeatsFilled+1),(train.get(l).noOfWaitingSeatsFilled-1),train.get(l).source,train.get(l).dest);
                                            train.set(l,t);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                passengerTicket.remove(i);
                break;
            }
        }
    }
    static void delete(int pnr){
        int c=passengerTicket.size();
        int b=0;
        for(int i=0;i<c-b;i++){

            if(passengerTicket.get(i).pnr==pnr){
                int sno=passengerTicket.get(i).seatNumber;
                int t1=passengerTicket.get(i).trainNumber;
                for(int j=0;j<passenger.size();j++){
                    if(passenger.get(j).p_id==passengerTicket.get(i).p_id){
                        passenger.remove(j);
                        break;
                    }
                }
                for(int j=0;j<train.size();j++){
                    if(passengerTicket.get(i).trainNumber==train.get(j).trainNumber){
                        if(passengerTicket.get(i).seatNumber==0){
                            Train t=new Train(train.get(j).trainNumber,train.get(j).station,train.get(j).noOfSeats,train.get(j).noOfWaitingListSeats,train.get(j).noOfSeatsFilled,(train.get(j).noOfWaitingSeatsFilled-1),train.get(j).source,train.get(j).dest);
                            train.set(j,t);
                            break;
                        }
                        else{
                            Train t=new Train(train.get(j).trainNumber,train.get(j).station,train.get(j).noOfSeats,train.get(j).noOfWaitingListSeats,(train.get(j).noOfSeatsFilled-1),train.get(j).noOfWaitingSeatsFilled,train.get(j).source,train.get(j).dest);
                            train.set(j,t);
                            break;
                        }
                    }
                }
                passengerTicket.remove(i);
                b++;
                i--;
                for(int k=0;k<passengerTicket.size();k++){
                    if(passengerTicket.get(k).trainNumber==t1 && passengerTicket.get(k).seatNumber==0){
                        PassengerTicket p=new PassengerTicket(passengerTicket.get(k).pt_id,passengerTicket.get(k).p_id,sno,passengerTicket.get(k).pnr,passengerTicket.get(k).trainNumber);
                        passengerTicket.set(k,p);
                        for(int z=0;z<train.size();z++){
                            if(train.get(z).trainNumber==passengerTicket.get(k).trainNumber){
                                Train t=new Train(train.get(z).trainNumber,train.get(z).station,train.get(z).noOfSeats,train.get(z).noOfWaitingListSeats,(train.get(z).noOfSeatsFilled+1),(train.get(z).noOfWaitingSeatsFilled-1),train.get(z).source,train.get(z).dest);
                                train.set(z,t);

                            }
                        }
                        break;
                    }
                }
            }
        }
        c=ticket.size();
        b=0;
        for(int i=0;i<c-b;i++){
            if(ticket.get(i).pnr==pnr){
                ticket.remove(i);
                i--;
                b++;
            }
        }
    }

    static int book(String source,String dest){
        System.out.println("Enter Number of Tickets ");
        int n=scan.nextInt();
        int count=0;int remain=0;
        for(int i=0;i<b.size();i++){
            for(int j=0;j<train.size();j++) {
                if(train.get(j).trainNumber==b.get(i)) {
                    remain = ((train.get(j).noOfSeats + train.get(j).noOfWaitingListSeats)- (train.get(j).noOfSeatsFilled + train.get(j).noOfWaitingSeatsFilled));

                    if (n <= remain) {
                        count++;
                    }
                }
            }
        }

        if(count==b.size()){
            pnr++;
            Ticket t=new Ticket(pnr,source,dest);
            ticket.add(t);

            while(n!=0){
                System.out.println("Enter the Passenger name");
                String name=scan.next();
                p_id++;
                Passenger p=new Passenger(p_id,name);
                passenger.add(p);
                for(int i=0;i<b.size();i++){
                    for(int j=0;j<train.size();j++){
                        if(train.get(j).trainNumber==b.get(i)){
                            if(train.get(j).noOfSeatsFilled<train.get(j).noOfSeats){
                                int seat=findseat(j,train.get(j).trainNumber);
                                pt_id++;
                                PassengerTicket p1=new PassengerTicket(pt_id,p_id,seat,pnr,train.get(j).trainNumber);
                                passengerTicket.add(p1);
                                train.get(j).noOfSeatsFilled=train.get(j).noOfSeatsFilled+1;
                            }
                            else{
                                pt_id++;
                                PassengerTicket p1=new PassengerTicket(pt_id,p_id,0,pnr,train.get(j).trainNumber);
                                passengerTicket.add(p1);
                                train.get(j).noOfWaitingSeatsFilled=train.get(j).noOfWaitingSeatsFilled+1;
                            }
                        }
                    }
                }
                n--;
            }
            System.out.println("Your Ticket is Successfully Booked");
            displayy(pnr+1);
            return 1;
        }
        else{
            return 0;
        }
    }
    static int rec(String source, String dest, int f) {
        for (int i = 0; i < train.size(); i++) {
            if(i!=f) {
                int flag=0;
                String s1 = train.get(i).station;
                if(s1.contains(source) && s1.contains(dest)){
                    b.add(train.get(i).trainNumber);
                    return 1;
                }
                else if(s1.contains(source)) {
                    s1 = s1.substring(1, s1.length() - 1);
                    b.add(train.get(i).trainNumber);

                    for (int j = 0; j < train.size(); j++) {
                        if (j != i && j!=f) {


                            String s2 = train.get(j).station;
                            s2 = s2.substring(1, s2.length() - 1);
                            String[] str1 = s1.split(",");
                            String[] str2 = s2.split(",");
                            if(comparer(str1,str2)==1 && comp(str2,dest)==1){
                                b.add(train.get(j).trainNumber);
                                transiter.add(equaler(str2,str1));
                                return 1;
                            }
                            else if(comparer(str1,str2)==1){
                                transiter.add(equaler(str2,str1));
                                b.add(train.get(j).trainNumber);
                                return rec(equaler(str2,str1),dest,i);

                            }

                        }
                    }
                }
            }
        }
        return 0;
    }

    static int comparer(String[] str1,String[] str2){


        for(int i=0;i<str1.length;i++){
            for(int j=0;j<str2.length;j++){
                if(str1[i].equals(str2[j]))
                {
                    //System.out.println(str1[i]);
                    return 1;
                }
            }
        }
        return 0;
    }
    static int comp(String[] str1,String s){
        for(int i=0;i<str1.length;i++){
            if(str1[i].equals(s)){
                //System.out.println(str1[i]);
                return 1;
            }
        }
        return 0;
    }
    static String equaler(String[] str1,String[] str2){
        for(int i=0;i<str1.length;i++){
            for(int j=0;j<str2.length;j++){
                if(str1[i].equals(str2[j]))
                {
                    return str1[i];
                    //System.out.println(str1[i]);
                }
            }
        }
        return null;
    }
    static Integer findseat(int j,int t){
        for(int i=1;i<=train.get(j).noOfSeats;i++){
            int flag=0;
            for(int k=0;k<passengerTicket.size();k++){
                if(passengerTicket.get(k).trainNumber==t){
                    if(passengerTicket.get(k).seatNumber==i){
                        flag=1;
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
        ResultSet rs;
        stm.executeUpdate("CREATE TABLE passenger(p_id INT PRIMARY KEY,name VARCHAR(20))");
        stm.executeUpdate("CREATE TABLE ticket( pnr INT PRIMARY KEY,source VARCHAR(20),dest VARCHAR(20) )");
        stm.executeUpdate("CREATE TABLE passengerticket( pt_id INT PRIMARY KEY,p_id INT ,seatNumber INT,pnr INT,trainNumber INT )");
        stm.executeUpdate("CREATE TABLE train(trainNumber INT PRIMARY KEY,station VARCHAR,noOfSeats INT,noOfWaitingListSeats INT,noOfSeatsFilled INT,noOfWaitingListSeatsFilled INT,source VARCHAR,dest VARCHAR)");
        stm.executeUpdate("INSERT INTO train VALUES(1,'(A,B,C,D,E)',8,2,0,0,'A','E')");
        stm.executeUpdate("INSERT INTO train VALUES(2,'(X,Y,C)',8,2,0,0,'X','C')");
        stm.executeUpdate("INSERT INTO train VALUES(3,'(W,R,X)',8,2,0,0,'W','X')");
        stm.executeUpdate("INSERT INTO train VALUES(4,'(Q,Z,W)',8,2,0,0,'Q','W')");
        stm.executeUpdate("INSERT INTO train VALUES(5,'(N,M,Q)',8,2,0,0,'N','Q')");
        stm.executeUpdate("INSERT INTO train VALUES(6,'(F,P,N)',8,2,0,0,'F','N')");
        stm.executeUpdate("INSERT INTO train VALUES(7,'(K,S,F)',8,2,0,0,'Z','F')");
        stm.executeUpdate("INSERT INTO train VALUES(8,'(T,U,K)',8,2,0,0,'T','Z')");
        stm.executeUpdate("INSERT INTO train VALUES(9,'(V,I,T)',8,2,0,0,'V','T')");
        stm.executeUpdate("INSERT INTO train VALUES(10,'(G,H,V)',8,2,0,0,'G','V')");
        rs= stm.executeQuery("SELECT * FROM train");

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
        rs=stm.executeQuery("SELECT * FROM passengerticket");
        while(rs.next()){
            PassengerTicket p=new PassengerTicket(rs.getInt("pt_id"),rs.getInt("p_id"),rs.getInt("seatNumber"),rs.getInt("pnr"),rs.getInt("trainNumber"));
            passengerTicket.add(p);
        }
        // System.out.println(passengerTicket.size());
    }
    static void copyback(Statement stm,int v) throws SQLException {
        stm.executeUpdate("DELETE FROM train");
        stm.executeUpdate("DELETE FROM passenger");
        stm.executeUpdate("DELETE FROM passengerticket");
        stm.executeUpdate("DELETE FROM ticket");
        for(int i=0;i<train.size();i++){
            stm.executeUpdate("INSERT INTO train VALUES("+train.get(i).trainNumber+",'"+train.get(i).station+"',"+train.get(i).noOfSeats+","+train.get(i).noOfWaitingListSeats+","+train.get(i).noOfSeatsFilled+","+train.get(i).noOfWaitingSeatsFilled+",'"+train.get(i).source+"','"+train.get(i).dest+"')");
        }
        for(int i=0;i<passenger.size();i++){
            stm.executeUpdate("INSERT INTO passenger VALUES("+passenger.get(i).p_id+",'"+passenger.get(i).name+"')");
        }
        for(int i=0;i<passengerTicket.size();i++){
            stm.executeUpdate("INSERT INTO passengerticket VALUES("+passengerTicket.get(i).pt_id+","+passengerTicket.get(i).p_id+","+passengerTicket.get(i).seatNumber+","+passengerTicket.get(i).pnr+","+passengerTicket.get(i).trainNumber+")");
        }
        for(int i=0;i<ticket.size();i++){
            stm.executeUpdate("INSERT INTO ticket VALUES("+ticket.get(i).pnr+",'"+ticket.get(i).source+"','"+ticket.get(i).dest+"')");
        }
        if(v==1) {
//            ResultSet rs;
//            rs=stm.executeQuery("SELECT * FROM train");
//            while(rs.next())
//            {
//                System.out.println(rs.getInt("trainNumber")+" "+rs.getString("station")+" "+rs.getInt("noOfSeats")+" "+rs.getInt("noOfWaitingListSeats")+" "+rs.getInt("noOfSeatsFilled")+" "+rs.getInt("noOfWaitingListSeatsFilled")+" "+rs.getString("source")+" "+rs.getString("dest"));
//
//            }
//            rs=stm.executeQuery("SELECT * FROM passenger");
//            while(rs.next()){
//                System.out.println(rs.getInt("p_id")+" "+rs.getString("name"));
//
//            }
//            rs=stm.executeQuery("SELECT * FROM ticket");
//            while(rs.next()){
//                System.out.println(rs.getInt("pnr")+" "+rs.getString("source")+" "+rs.getString("dest"));
//
//            }
//            rs=stm.executeQuery("SELECT * FROM passengerticket");
//            while(rs.next()){
//                System.out.println(rs.getInt("pt_id")+" "+rs.getInt("p_id")+" "+rs.getInt("seatNumber")+" "+rs.getInt("pnr")+" "+rs.getInt("trainNumber"));
//
//            }
            for(int i=0;i<train.size();i++){
                System.out.println(train.get(i).trainNumber+"\t"+train.get(i).station+"\t"+train.get(i).noOfSeats+"\t"+train.get(i).noOfWaitingListSeats+"\t"+train.get(i).noOfSeatsFilled+"\t"+train.get(i).noOfWaitingSeatsFilled+"\t"+train.get(i).source+"\t"+train.get(i).dest);
            }
            for(int i=0;i<passenger.size();i++){
                System.out.println(passenger.get(i).p_id+"\t"+passenger.get(i).name);
            }
            for(int i=0;i<passengerTicket.size();i++){
                System.out.println(passengerTicket.get(i).pt_id+"\t"+passengerTicket.get(i).p_id+"\t"+passengerTicket.get(i).seatNumber+"\t"+passengerTicket.get(i).pnr+"\t"+passengerTicket.get(i).trainNumber);
            }
            for(int i=0;i<ticket.size();i++){
                System.out.println(ticket.get(i).pnr+"\t"+ticket.get(i).source+"\t"+ticket.get(i).dest);
            }
        }
    }

    static void occupancy(){
        for(int i=0;i<train.size();i++){
            System.out.println("Train Number:"+train.get(i).trainNumber);
            for(int j=0;j<passengerTicket.size();j++){
                if(passengerTicket.get(j).trainNumber==train.get(i).trainNumber){
                    for(int k=0;k<passenger.size();k++){
                        if(passengerTicket.get(j).p_id==passenger.get(k).p_id){
                            if(passengerTicket.get(j).seatNumber==0){
                                System.out.println("Name:"+passenger.get(k).name+"\t"+"Seat number: Waiting List");
                            }
                            else {
                                System.out.println("Name:" + passenger.get(k).name + "\t" + "Seat number:" + passengerTicket.get(j).seatNumber);
                            }
                        }
                    }
                }
            }
            System.out.println();
        }
    }
    static ArrayList displayy(int p){
        int d=p-1;
        ArrayList<Integer> a=new ArrayList<Integer>();
        int serial=1;
        for(int i=0;i<ticket.size();i++){
            if(ticket.get(i).pnr==d){
                System.out.println("PNR:"+ticket.get(i).pnr);
                System.out.println("Source:"+ticket.get(i).source);
                System.out.println("Destination:"+ticket.get(i).dest);
                for(int j=0;j<b.size();j++){
                    System.out.println("Train Number:"+b.get(j));
                    for(int k=0;k<passengerTicket.size();k++){
                        if(passengerTicket.get(k).trainNumber==b.get(j) && passengerTicket.get(k).pnr==d && passengerTicket.get(k).seatNumber!=0){
                            a.add(passengerTicket.get(k).pt_id);
                            for(int l=0;l<passenger.size();l++){
                                if(passenger.get(l).p_id==passengerTicket.get(k).p_id){
                                    System.out.println(serial+".Passenger Name:"+passenger.get(l).name+"\tSeat Number:"+passengerTicket.get(k).seatNumber);
                                    serial++;
                                }
                            }
                        }
                        else if(passengerTicket.get(k).trainNumber==b.get(j) && passengerTicket.get(k).pnr==d && passengerTicket.get(k).seatNumber==0){
                            a.add(passengerTicket.get(k).pt_id);
                            for(int l=0;l<passenger.size();l++){
                                if(passenger.get(l).p_id==passengerTicket.get(k).p_id){
                                    System.out.println(serial+".Passenger Name:"+passenger.get(l).name+"\tSeat Number: Waiting List");
                                    serial++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return a;
    }
}

