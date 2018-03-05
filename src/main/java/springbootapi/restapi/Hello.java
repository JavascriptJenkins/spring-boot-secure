package springbootapi.restapi;

import org.springframework.stereotype.Component;
import springbootapi.objects.HelloObject;

import java.util.ArrayList;


@Component
public interface Hello {


    ArrayList<HelloObject> getHelloObjects();




}
