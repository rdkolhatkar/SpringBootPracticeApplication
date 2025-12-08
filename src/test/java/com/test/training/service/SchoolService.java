package com.test.training.service;

import com.test.training.config.HibernateUtil;
import com.test.training.entity.School;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolService {

    // ------------------- ADD SCHOOL -------------------
    public String addSchool(School school) {
        if (school.getSchool_name() == null || school.getSchool_name().trim().isEmpty()) {
            return "NAME_REQUIRED";
        }
        if (school.getSchool_address() == null || school.getSchool_address().trim().isEmpty()) {
            return "ADDRESS_REQUIRED";
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Check if school already exists by name
            Query<School> query = session.createQuery(
                    "FROM School WHERE school_name = :name", School.class);
            query.setParameter("name", school.getSchool_name().trim());
            List<School> existing = query.list();
            if (!existing.isEmpty()) {
                return "EXISTS";
            }

            session.save(school);
            transaction.commit();
            return "CREATED";
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return "ERROR";
        }
    }

    // ------------------- GET ALL SCHOOLS -------------------
    public List<School> getAllSchools() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM School", School.class).list();
        }
    }

    // ------------------- DELETE SCHOOL -------------------
    public boolean deleteSchool(Integer id, String schoolName) {
        if ((id == null) && (schoolName == null || schoolName.trim().isEmpty())) {
            return false;
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            School school = null;
            if (id != null) {
                school = session.get(School.class, Long.valueOf(id));
            }
            if (school == null && schoolName != null) {
                Query<School> query = session.createQuery(
                        "FROM School WHERE school_name = :name", School.class);
                query.setParameter("name", schoolName.trim());
                List<School> list = query.list();
                if (!list.isEmpty()) school = list.get(0);
            }

            if (school == null) {
                return false;
            }

            session.delete(school);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // ------------------- UPDATE SCHOOL ADDRESS -------------------
    public String updateAddress(Integer id, String newAddress) {
        if (newAddress == null || newAddress.trim().isEmpty()) {
            return "INVALID_ADDRESS";
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            School school = session.get(School.class, Long.valueOf(id));
            if (school == null) {
                return "NOT_FOUND";
            }

            school.setSchool_address(newAddress.trim());
            session.update(school);
            transaction.commit();
            return "UPDATED";
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return "ERROR";
        }
    }
}
