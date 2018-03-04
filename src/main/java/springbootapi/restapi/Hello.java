package springbootapi.restapi;

import springbootapi.objects.HelloObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public interface Hello {


    ArrayList<HelloObject> getHelloObjects();




}
