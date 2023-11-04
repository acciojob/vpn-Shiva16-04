package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        Optional<User>optionalUser=userRepository2.findById(userId);
        User user=optionalUser.get();

        //handling case 1
        if(user.getConnected()){
            throw new Exception("Already connected");
        }

        //handling case 2
        String inputCountryName=countryName.toUpperCase();
        String userCountryName=user.getOriginalCountry().getCountryName().toString();
        if(inputCountryName.equals(userCountryName)){
            return user;
        }

        //handling case 3
        ServiceProvider serviceProvider=null;
        Country country=null;
        int min=Integer.MAX_VALUE;

        for(ServiceProvider serviceProviderValue: user.getServiceProviderList()){
            for(Country countryValue: serviceProviderValue.getCountryList()){
                if(countryValue.getCountryName().toString().equals(inputCountryName) && serviceProviderValue.getId()<min){
                    serviceProvider=serviceProviderValue;
                    country=countryValue;
                    min=serviceProviderValue.getId();
                }
            }
//            if(serviceProvider!=null)break;
        }
        if(serviceProvider==null){
            throw new Exception("Unable to connect");
        }

        Connection connection=new Connection();
        //setting foreign keys
        connection.setUser(user);
        connection.setServiceProvider(serviceProvider);

        //bidirectionally mapping
        serviceProvider.getConnectionList().add(connection);
        user.getConnectionList().add(connection);
        user.setConnected(true); //error rectified from de-referencing
        user.setMaskedIp(""+country.getCountryName().toCode()+"."+serviceProvider.getId()+"."+userId);

        userRepository2.save(user);
        serviceProviderRepository2.save(serviceProvider);
        //connectionRepository2.save(connection);

        return user;

    }
    @Override
    public User disconnect(int userId) throws Exception {
        Optional<User>optionalUser=userRepository2.findById(userId);
        User user=optionalUser.get();
        if(!user.getConnected()){
            throw new Exception("Already disconnected");
        }
        user.setConnected(false); //error rectified from de-referencing
        user.setMaskedIp(null);
        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender=userRepository2.findById(senderId).get();
        User receiver=userRepository2.findById(receiverId).get();

        String senderCountry=sender.getOriginalCountry().getCountryName().toString();
        String receiverCountry=null;
        if(receiver.getConnected()){
            String code=receiver.getMaskedIp().substring(0,3);
            switch (code){
                case "001": receiverCountry="IND"; break;
                case "002": receiverCountry="USA"; break;
                case "003": receiverCountry="AUS"; break;
                case "004": receiverCountry="CHI"; break;
                case "005": receiverCountry="JPN"; break;
            }
        }else{
            receiverCountry=receiver.getOriginalCountry().getCountryName().toString();
        }
        if(senderCountry.equals(receiverCountry)){
            return sender;
        }
        User user=connect(senderId, receiverCountry);
        if(!user.getConnected()){
            throw new Exception("Cannot establish communication");
        }
        return user;
    }
}
