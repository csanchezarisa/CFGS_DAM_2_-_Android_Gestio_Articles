package com.example.gestioarticles.assets.datetype;

public class Date {

    public String day;
    public String month;
    public String year;

    public Date(int day, int month, int year) {
        this.day = dosDigits(day);
        this.month = dosDigits(month);
        this.year = String.valueOf(year);
    }

    public String getEuropeanDate() {
        return this.day + "/" + this.month + "/" + this.year;
    }

    public String getSQLDate() {
        return this.year + "/" + this.month + "/" + this.day;
    }

    public static String dosDigits(int number) {
        return (number < 9) ? ("0" + number) : String.valueOf(number);
    }
}
