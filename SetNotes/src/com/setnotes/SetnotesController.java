package com.setnotes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
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
public class SetnotesController {
	@RequestMapping("/goauth")
	public ModelAndView goauth_ctl() {
		return new ModelAndView(
				"redirect:https://accounts.google.com/o/oauth2/auth?redirect_uri=http://localhost:8888/get_authz_code&response_type=code&client_id=861048048812-hb0m83oek2msngnu5kf2t8gd2b9enf0a.apps.googleusercontent.com&approval_prompt=force&scope=email&access_type=online");
	}

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
	      
	      String userEmail = user_info.get("email").getAsString();
	      String userName =  user_info.get("name").getAsString();
	      writer.close();
	      reader.close();
	      Signup sp = new Signup();
	      PersistenceManager pm = PMF.get().getPersistenceManager(); 
	      
	      if (userEmail != null) {
				Query q = (Query) pm.newQuery(Signup.class);
				q.setFilter(" email == '" + userEmail + "'");
				@SuppressWarnings("unchecked")
				List<Signup> spData = (List<Signup>) q.execute();
				if (!(spData.isEmpty())) {
					System.out.println("to prevent from null");
				} else {
					  sp.setName(userName);
				      sp.setEmail(userEmail);
				      pm.makePersistent(sp);
				}
			}
	      
	      return new ModelAndView(
		    		"profile.jsp?name=" + user_info.get("name") + "&email=" + user_info.get("email")); 

	}
    
//Signup form controller
	@RequestMapping("/signup")  
    public ModelAndView signup_ctl(HttpServletRequest req, HttpServletResponse resp) {
    	String fname= req.getParameter("firstname");
    	String email= req.getParameter("email");
    	String password= req.getParameter("password");
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();
    	
    	Signup sp = new Signup();
    	sp.setName(fname);
    	sp.setEmail(email);
    	sp.setPassword(password);
    	
    	pm.makePersistent(sp);
    	
        return new ModelAndView("profile");
    }
    
//Login form controller
	@RequestMapping("/login")  
    public ModelAndView login_ctl() {
        return new ModelAndView("profile");
    }
}

