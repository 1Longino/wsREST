package mx.com.longino.config;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import mx.com.longino.service.WsRestPlayer;


@ApplicationPath("services")
public class InizializerAplication extends Application {
	
	@Override
	public Set<Class<?>> getClasses(){
		
		 Set<Class<?>> classes = new HashSet<Class<?>>();
		 classes.add(WsRestPlayer.class);
		
		return classes;
		
	}
	

}
