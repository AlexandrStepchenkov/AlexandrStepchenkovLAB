package com.example.carsharing.service;

import com.example.carsharing.model.*;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Transactional
public class DB {

    @PersistenceContext
    private EntityManager em;

    public User saveUser(int id, String name, String email, String phone) {
        User u = em.find(User.class, id);
        if (u == null) u = new User(id, name, email, phone);
        else { u.setName(name); u.setEmail(email); u.setPhone(phone); }
        return em.merge(u);
    }

    public User updateUser(int userId, String name, String email, String phone) {
        User u = em.find(User.class, userId);
        if (u == null) return null;
        if (name != null) u.setName(name);
        if (email != null) u.setEmail(email);
        if (phone != null) u.setPhone(phone);
        return em.merge(u);
    }

    public User getUserById(int id) {
        return em.find(User.class, id);
    }

    public List<User> users() {
        return em.createQuery("from User", User.class).getResultList();
    }

    public List<User> findUsers(String name, String email) {
        String query = "from User u where 1=1";
        if (name != null && !name.isEmpty()) {
            query += " and lower(u.name) like lower(concat('%', :name, '%'))";
        }
        if (email != null && !email.isEmpty()) {
            query += " and lower(u.email) like lower(concat('%', :email, '%'))";
        }

        TypedQuery<User> q = em.createQuery(query, User.class);
        if (name != null && !name.isEmpty()) {
            q.setParameter("name", name);
        }
        if (email != null && !email.isEmpty()) {
            q.setParameter("email", email);
        }
        return q.getResultList();
    }

    public User findUserByEmail(String email) {
        try {
            return em.createQuery("from User u where u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void deleteUser(int id) {
        em.createQuery("delete from Ride r where r.userId = :id")
                .setParameter("id", id)
                .executeUpdate();

        User u = em.find(User.class, id);
        if (u != null) em.remove(u);
    }

    public Car saveCar(int id, String model, String plate, double price) {
        Car c = em.find(Car.class, id);
        if (c == null) c = new Car(id, model, plate, price, true);
        else { c.setModel(model); c.setLicensePlate(plate); c.setPricePerKm(price); }
        return em.merge(c);
    }

    public Car updateCar(int carId, String model, String licensePlate, Double pricePerKm, Boolean available) {
        Car c = em.find(Car.class, carId);
        if (c == null) return null;
        if (model != null) c.setModel(model);
        if (licensePlate != null) c.setLicensePlate(licensePlate);
        if (pricePerKm != null) c.setPricePerKm(pricePerKm);
        if (available != null) c.setAvailable(available);
        return em.merge(c);
    }

    public Car updateCarAvailability(int carId, boolean available) {
        Car c = em.find(Car.class, carId);
        if (c == null) return null;
        c.setAvailable(available);
        return em.merge(c);
    }

    public Car getCarById(int id) {
        return em.find(Car.class, id);
    }

    public List<Car> cars() {
        return em.createQuery("from Car", Car.class).getResultList();
    }

    public List<Car> findCarsByModel(String model) {
        return em.createQuery("from Car c where lower(c.model) like lower(concat('%', :model, '%'))", Car.class)
                .setParameter("model", model)
                .getResultList();
    }

    public void deleteCar(int id) {
        em.createQuery("update Car c set c.available = false where c.carId = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public Ride addRide(int userId, int carId, double dist, double hours) {
        Car car = em.find(Car.class, carId);
        if (car == null || !car.isAvailable()) return null;

        Ride r = new Ride(null, userId, carId, dist, hours, "CREATED");
        em.persist(r);
        return r;
    }

    public Ride getRideById(Long id) {
        return em.find(Ride.class, id);
    }

    public List<Ride> rides() {
        return em.createQuery("from Ride", Ride.class).getResultList();
    }

    public List<Ride> ridesByUser(int userId) {
        return em.createQuery("from Ride r where r.userId = :uid", Ride.class)
                .setParameter("uid", userId)
                .getResultList();
    }

    public List<Ride> ridesByCar(int carId) {
        return em.createQuery("from Ride r where r.carId = :cid", Ride.class)
                .setParameter("cid", carId)
                .getResultList();
    }

    public List<Ride> ridesByStatus(String status) {
        return em.createQuery("from Ride r where r.status = :status", Ride.class)
                .setParameter("status", status)
                .getResultList();
    }

    public Ride startRide(Long id) {
        Ride r = em.find(Ride.class, id);
        if (r == null || !"CREATED".equals(r.getStatus())) return null;
        Car c = em.find(Car.class, r.getCarId());
        if (c == null || !c.isAvailable()) return null;

        c.setAvailable(false);
        r.setStatus("IN_PROGRESS");
        em.merge(c);
        return em.merge(r);
    }

    public Ride updateRide(Long rideId, Double distanceKm, Double durationHours) {
        Ride r = em.find(Ride.class, rideId);
        if (r == null) return null;
        if (distanceKm != null && distanceKm > 0) r.setDistanceKm(distanceKm);
        if (durationHours != null && durationHours > 0) r.setDurationHours(durationHours);
        return em.merge(r);
    }

    public Payment completeRide(Long id) {
        Ride r = em.find(Ride.class, id);
        if (r == null || !"IN_PROGRESS".equals(r.getStatus())) return null;

        Car c = em.find(Car.class, r.getCarId());
        if (c != null) {
            c.setAvailable(true);
            em.merge(c);
        }

        r.setStatus("COMPLETED");
        em.merge(r);

        Payment p = new Payment();
        p.setRideId(id);
        p.setAmount(r.getDistanceKm() * c.getPricePerKm());
        em.persist(p);
        return p;
    }

    public String deleteRide(Long id) {
        Ride r = em.find(Ride.class, id);
        if (r == null) return "Ride not found";

        if ("IN_PROGRESS".equals(r.getStatus())) {
            Car c = em.find(Car.class, r.getCarId());
            if (c != null) {
                c.setAvailable(true);
                em.merge(c);
            }
        }

        em.createQuery("delete from Payment p where p.rideId = :rideId")
                .setParameter("rideId", id)
                .executeUpdate();

        em.remove(r);
        return "Ride deleted successfully";
    }

    public Object cancelRide(Long rideId) {
        Ride r = em.find(Ride.class, rideId);
        if (r == null) return "Ride not found";

        if ("CREATED".equals(r.getStatus()) || "IN_PROGRESS".equals(r.getStatus())) {
            Car c = em.find(Car.class, r.getCarId());
            if (c != null) {
                c.setAvailable(true);
                em.merge(c);
            }

            em.remove(r);
            return "Ride cancelled successfully";
        }
        return "Cannot cancel completed ride";
    }

    public Payment getPaymentById(Long id) {
        return em.find(Payment.class, id);
    }

    public Payment getPaymentByRideId(Long rideId) {
        try {
            return em.createQuery("from Payment p where p.rideId = :rideId", Payment.class)
                    .setParameter("rideId", rideId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Payment> payments() {
        return em.createQuery("from Payment", Payment.class).getResultList();
    }

    public List<Payment> findPaymentsByAmount(Double minAmount, Double maxAmount) {
        String query = "from Payment p where 1=1";
        if (minAmount != null) query += " and p.amount >= :minAmount";
        if (maxAmount != null) query += " and p.amount <= :maxAmount";

        TypedQuery<Payment> q = em.createQuery(query, Payment.class);
        if (minAmount != null) q.setParameter("minAmount", minAmount);
        if (maxAmount != null) q.setParameter("maxAmount", maxAmount);

        return q.getResultList();
    }

    public Payment createManualPayment(Long rideId, double amount) {
        Ride r = em.find(Ride.class, rideId);
        if (r == null) return null;
        if (!"COMPLETED".equals(r.getStatus())) return null;

        Payment existing = getPaymentByRideId(rideId);
        if (existing != null) return null;

        Payment p = new Payment();
        p.setRideId(rideId);
        p.setAmount(amount);
        em.persist(p);
        return p;
    }

    public Payment updatePayment(Long paymentId, Double amount) {
        Payment p = em.find(Payment.class, paymentId);
        if (p == null) return null;
        if (amount != null && amount >= 0) {
            p.setAmount(amount);
            return em.merge(p);
        }
        return p;
    }

    public String deletePayment(Long id) {
        Payment p = em.find(Payment.class, id);
        if (p == null) return "Payment not found";
        em.remove(p);
        return "Payment deleted successfully";
    }

    public List<Car> availableCars() {
        return em.createQuery("from Car c where c.available = true", Car.class).getResultList();
    }

    public double totalRevenue() {
        return em.createQuery("select coalesce(sum(p.amount), 0) from Payment p", Double.class)
                .getSingleResult();
    }

    public int activeRidesCount() {
        return em.createQuery("select count(r) from Ride r where r.status = 'IN_PROGRESS'", Long.class)
                .getSingleResult().intValue();
    }

    public List<Car> busyCars() {
        return em.createQuery(
                "select c from Car c where c.carId in (select r.carId from Ride r where r.status = 'IN_PROGRESS')",
                Car.class).getResultList();
    }

    public Map<String, Object> getUserRideStats(int userId) {
        Object[] stats = em.createQuery(
                        "select count(r), sum(r.distanceKm), sum(r.durationHours) " +
                                "from Ride r where r.userId = :userId and r.status = 'COMPLETED'", Object[].class)
                .setParameter("userId", userId)
                .getSingleResult();

        Map<String, Object> result = new HashMap<>();
        result.put("total_rides", stats[0] != null ? stats[0] : 0);
        result.put("total_distance", stats[1] != null ? stats[1] : 0.0);
        result.put("total_hours", stats[2] != null ? stats[2] : 0.0);
        return result;
    }

    public Map<String, Object> getCarRideStats(int carId) {
        Object[] stats = em.createQuery(
                        "select count(r), sum(r.distanceKm), sum(r.durationHours), " +
                                "coalesce(sum(r.distanceKm * c.pricePerKm), 0) " +
                                "from Ride r join Car c on r.carId = c.carId " +
                                "where r.carId = :carId and r.status = 'COMPLETED'", Object[].class)
                .setParameter("carId", carId)
                .getSingleResult();

        Map<String, Object> result = new HashMap<>();
        result.put("total_rides", stats[0] != null ? stats[0] : 0);
        result.put("total_distance", stats[1] != null ? stats[1] : 0.0);
        result.put("total_hours", stats[2] != null ? stats[2] : 0.0);
        result.put("total_revenue", stats[3] != null ? stats[3] : 0.0);
        return result;
    }

    public List<Map<String, Object>> getMonthlyRevenue() {
        Map<String, Object> stub = new HashMap<>();
        stub.put("month", "Not implemented");
        stub.put("revenue", 0.0);
        return List.of(stub);
    }
}