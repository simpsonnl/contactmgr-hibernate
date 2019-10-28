package com.nicksimpson.contactmgr;

import com.nicksimpson.contactmgr.model.Contact;
import com.nicksimpson.contactmgr.model.Contact.ContactBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;


public class Application {

    //hold a reusable reference to a session factory (only need one)
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory(){
        //create a standard service registry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {
        Contact contact = new ContactBuilder("Nick", "Simpson")
                .withEmail("simpsonnl22@gmail.com")
                .withPhone(7043157288L)
                .build();

       int id = save(contact);

       //display a list of contacts before update
        fetchAllContacts().forEach(System.out::println);

        //get persisted contact
        Contact c = findContactById(id);

        //update the contact
        c.setFirstName("Nick");

        //persist changes
        update(c);

        //display a list of contacts after the update
        fetchAllContacts().forEach(System.out::println);


        // Get the contact with id of 1
        c = findContactById(2);

        // Delete the contact
        System.out.printf("%nDeleting...%n");
        //prints out a message if no matching id is found
        try {
            delete(c);
            System.out.printf("%nDeleted!%n");
        }catch (Exception e){
            System.out.printf("%nContact does not exist%n");
        }
        System.out.printf("%nAfter delete%n");
        fetchAllContacts().forEach(System.out::println);
    }

    private static Contact findContactById(int id){
        //open session
        Session session = sessionFactory.openSession();

        //retrieve persistent contact if exists
        Contact contact = session.get(Contact.class,id);

        //close session
        session.close();

        //return contact object
        return contact;
    }

    private static void update(Contact contact){
        //open session
        Session session = sessionFactory.openSession();

        //begin transaction
        session.beginTransaction();

        //use session to update
        session.update(contact);

        //commit transaction
        session.getTransaction().commit();

        //close session
        session.close();
    }

    private static List<Contact> fetchAllContacts(){
        //open session
        Session session = sessionFactory.openSession();

        //create criteria builder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        //create criteria query
        CriteriaQuery<Contact> criteria = builder.createQuery(Contact.class);

        //specify criteria root
        criteria.from(Contact.class);

        //execute query
        List<Contact> contacts = session.createQuery(criteria).getResultList();

        //close session
       session.close();

       return contacts;
    }

    private static int save(Contact contact){
        //Open a session
        Session session = sessionFactory.openSession();

        //begin transaction
        session.beginTransaction();

        //use the session to save the contact
        int id = (int)session.save(contact);

        //commit transaction
        session.getTransaction().commit();

        //close the session
        session.close();

        return id;
    }

    private static void delete(Contact contact){

        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to update the contact

            session.delete(contact);


        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }
}
