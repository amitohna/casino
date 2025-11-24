package com.example.casino_israel;

public class players {
    private double wallet;
    private String name;
    private int id;

public players(double wallet, String name, int id) {
    this.wallet = wallet;
    this.name = name;
    this.id = id;
}

    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}