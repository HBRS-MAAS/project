package org.maas.objects;

import java.awt.geom.Point2D;
import java.util.Vector;

public class Client {
    private String guid;
    private int type;
    private String name;
    private Point2D location ;
    private Vector<OrderMas> orders;

    public Client() {}

    public Client(String guid, int type, String name, Point2D location, Vector<OrderMas> orders) {
        this.guid = guid;
        this.type = type;
        this.name = name;
        this.location = location;
        this.orders = orders;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point2D getLocation() {
        return location;
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }

    public Vector<OrderMas> getOrders() {
        return orders;
    }

    public void setOrders(Vector<OrderMas> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Client [guid=" + guid + ", type=" + type + ", name=" + name + ", location=" + location + ", orders="
                + orders + "]";
    }
}
