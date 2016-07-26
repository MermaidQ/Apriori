package com.qiuzi.gtja.util;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;

import com.qiuzi.gtja.entity.User;

public class DatabaseUtil {
	
	public static void add(List<User> users){

		SessionFactory sf = HibernateUtil.getSessionFactory();  
		Session session = sf.openSession();
		Transaction tran = null;
		
		for(int i = 0;i < users.size();i++){
		    
		    users.get(i).setId(i);
			session.save(users.get(i)); 
			if(i%1000 == 0){
			    tran = session.beginTransaction();
				session.flush();
				tran.commit();
				session.clear();
			}
		} 
        tran = session.beginTransaction();
        session.flush();
		tran.commit();
		session.close();
	 	//sf.close();
		System.out.println("success");
	}
}
