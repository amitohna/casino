package com.example.casino_israel;

public class players {
    private String id; // Remains String
    private String name;
    private double wallet; // Changed back to double

    public players() {
        // Default constructor required for Firebase
    }

    public players(String id, String name, double wallet) {
        this.id = id;
        this.name = name;
        this.wallet = wallet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }
}