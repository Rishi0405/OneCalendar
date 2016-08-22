package com.OneCalendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



@Controller
public class OneCalendarController {
//Home
	@RequestMapping("/")
	public ModelAndView home_ctl() {
		return new ModelAndView("home");
	}

//Google Oauth
	@RequestMapping("/goauth")
	public ModelAndView goauth_ctl() {
		return new ModelAndView(
				"redirect:https://accounts.google.com/o/oauth2/auth?redirect_uri=http://localhost:8888/get_authz_code&response_type=code&client_id=861048048812-hb0m83oek2msngnu5kf2t8gd2b9enf0a.apps.googleusercontent.com&approval_prompt=force&scope=email&access_type=online");
	}
//Authcode, accesstoken and user_info
	@RequestMapping("/get_authz_code")
	public ModelAndView getauthcode_ctl(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {

	   
	      String code = req.getParameter("code");
	      String urlParameters = "code=" + 
	        code + 
	        "&client_id=861048048812-hb0m83oek2msngnu5kf2t8gd2b9enf0a.apps.googleusercontent.com" +
	        "&client_secret=61qVVz_2qOvQSXRKGCvW35VL" + 
	        "&redirect_uri=http://localhost:8888/get_authz_code" +
	        "&grant_type=authorization_code";
	      URL url = new URL("https://accounts.google.com/o/oauth2/token");
	      URLConnection conn = url.openConnection();
	      conn.setDoOutput(true);
	      OutputStreamWriter writer = new OutputStreamWriter(
	        conn.getOutputStream());
	      writer.write(urlParameters);
	      writer.flush();
	      String line1 = "";
	      BufferedReader reader = new BufferedReader(new InputStreamReader(
	        conn.getInputStream()));
	      String line;
	      while ((line = reader.readLine()) != null)
	      {
	        line1 = line1 + line;
	      }
	      System.out.println(line1);
	      //getting access token
	      String s = null;
	      try
	      {
	        JsonObject json = (JsonObject)new JsonParser().parse(line1);
	        s = json.get("access_token").getAsString();
	      }
	      catch (Exception localException) {
	    	  
	      }
	    
	      //getting user info
	      url = new URL(
	        "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + s);
	      conn = url.openConnection();
	      line1 = "";
	      reader = new BufferedReader(new InputStreamReader(
	        conn.getInputStream()));
	      while ((line = reader.readLine()) != null) {
	        line1 = line1 + line;
	      }
	      JsonObject user_info = null;
	      try
	      {
	    	  user_info = (JsonObject)new JsonParser().parse(line1);
	      }
	      catch (Exception localException) {
	    	  
	      }
	      System.out.println(user_info);
	      String userEmail = user_info.get("email").getAsString();
	      String userName =  user_info.get("name").getAsString();
	      writer.close();
	      reader.close();
	      CustomerList cl = new CustomerList();
	      PersistenceManager pm = PMF.get().getPersistenceManager(); 
	      
				Query q = (Query) pm.newQuery(CustomerList.class);
				q.setFilter(" email == '" + userEmail + "'");
				@SuppressWarnings("unchecked")
				List<CustomerList> spData = (List<CustomerList>) q.execute();
				if (!(spData.isEmpty())) {
				}
				else{
					  cl.setType("Google");
					  cl.setName(userName);
				      cl.setEmail(userEmail);
				      pm.makePersistent(cl);
				}
	      
	      return new ModelAndView("profile"); 

	}
	
//Facebook Oauth
		@SuppressWarnings("deprecation")
		@RequestMapping("/foauth")
		public ModelAndView foauth_ctl(){
			return new ModelAndView(
					"redirect:http://www.facebook.com/dialog/oauth?client_id=583719895124488&redirect_uri=" + URLEncoder.encode("http://localhost:8888/facebookAuth") + "&scope=email");
		}
	
//Facebook accesstoken
	@RequestMapping("/facebookAuth")
	public ModelAndView face_ctl(HttpServletRequest req, HttpServletResponse res){
		
		String code = req.getParameter("code");
        if (code == null || code.equals("")) {
            // an error occurred, handle this
        }

        String token = null;
        try {
            String g = "https://graph.facebook.com/oauth/access_token?client_id=583719895124488&redirect_uri=" + URLEncoder.encode("http://localhost:8888/facebookAuth", "UTF-8") + "&client_secret=9cf2e2f89e0d25fc0221dc1758a56d1c&code=" + code;
            URL u = new URL(g);
            URLConnection c = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                b.append(inputLine + "\n");            
            in.close();
            token = b.toString();
            System.out.println(token);
            if (token.startsWith("{"))
                throw new Exception("error on requesting token: " + token + " with code: " + code);
        } catch (Exception e) {
        	e.printStackTrace();
        }

        String graph = null;
        try {
            String g = "https://graph.facebook.com/me?" + token;
            URL u = new URL(g);
            URLConnection c = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                b.append(inputLine + "\n");            
            in.close();
            graph = b.toString();
        } catch (Exception e) {
                e.printStackTrace();
        }

        String firstName;
        String email;
        try {
        	System.out.println(graph);
        	JsonObject json =(JsonObject) new JsonParser().parse(graph);
            firstName = json.get("name").getAsString();
            email = json.get("email").getAsString();
            CustomerList cl = new CustomerList();
  	      PersistenceManager pm = PMF.get().getPersistenceManager(); 
  	      
  				Query q = (Query) pm.newQuery(CustomerList.class);
  				q.setFilter(" email == '" + email + "'");
  				@SuppressWarnings("unchecked")
  				List<CustomerList> spData = (List<CustomerList>) q.execute();
  				if (!(spData.isEmpty())) {
  				
  				}else{
						cl.setType("Facebook");
						cl.setName(firstName);
						cl.setEmail(email);
						pm.makePersistent(cl);
					} 
    }
        catch (Exception e) {
            e.printStackTrace();
        }
		return new ModelAndView("profile");

	}
	
    
//Signup form
	@RequestMapping("/signup")  
    public ModelAndView signup_ctl(HttpServletRequest req, HttpServletResponse resp) {
    	String fname= req.getParameter("name");
    	String email= req.getParameter("email");
    	String password= req.getParameter("password");
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();
    	
    	CustomerList sp = new CustomerList();
    	sp.setName(fname);
    	sp.setEmail(email);
    	sp.setPassword(password);
    	
    	pm.makePersistent(sp);
    	
        return new ModelAndView("profile");
    }
    
//Login form
	@RequestMapping("/login")  
    public ModelAndView login_ctl(HttpServletRequest req, HttpServletResponse resp) {
		String usrEmail= req.getParameter("email");
    	String password= req.getParameter("psw");
    	PersistenceManager pm = PMF.get().getPersistenceManager();
//    	CustomerList cl = new CustomerList();
//      long id = cl.getId();
//       
//       CustomerList customerList = (CustomerList) pm.getObjectById(CustomerList.class, id);
//       customerList.setEmail(usrEmail);
//       customerList.setPassword(password);
  	
    	if (usrEmail != null) 
	      {
    			Query q = (Query) pm.newQuery(CustomerList.class);
				q.setFilter(" email == '" + usrEmail + "' && password =='" + password +"'");
				@SuppressWarnings("unchecked")
				List<CustomerList> spData = (List<CustomerList>) q.execute();
				
				if (!(spData.isEmpty())) {
					return new ModelAndView("profile");
					}
				}
	return null;   	
    }
}

