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
        if(optionalUser.isPresent()){
            User user=optionalUser.get();

            //handling case 1
            if(user.getConnected()){
                throw new Exception("Already Connected");
            }

            //handling case 2
            String inputCountryName=countryName.toUpperCase();
            String userCountryName=user.getOriginalCountry().getCountryName().toString();
            if(inputCountryName.equals(userCountryName)){
                return user;
            }

            //handling case 3
            ServiceProvider serviceProvider=null;
            for(ServiceProvider serviceProviderValue: user.getServiceProviderList()){
                for(Country country: serviceProviderValue.getCountryList()){
                    if(country.toString().equals(inputCountryName)){
                        serviceProvider=serviceProviderValue;
                        break;
                    }
                }
                if(serviceProvider!=null)break;
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
            user.setMaskedIp(""+CountryName.valueOf(inputCountryName).toCode()+"."+serviceProvider.getId()+"."+userId);

            connectionRepository2.save(connection);

            return user;
        }
        return null;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        Optional<User>optionalUser=userRepository2.findById(userId);
        if(optionalUser.isPresent()){
            User user=optionalUser.get();
            if(!user.getConnected()){
                throw new Exception("Already disconnected");
            }
            user.setConnected(false); //error rectified from de-referencing
            user.setMaskedIp(null);
            User savedUser=userRepository2.save(user);
            return savedUser;
        }
        return null;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        Optional<User>optionalSender=userRepository2.findById(senderId);
        Optional<User>optionalReceiver=userRepository2.findById(receiverId);
        if(optionalSender.isPresent() && optionalReceiver.isPresent()){
            User sender=optionalSender.get();
            User receiver=optionalReceiver.get();
            String senderCountry=sender.getOriginalCountry().toString();
            String receiverCountry=null;
            if(receiver.getConnected()){
                String code=receiver.getMaskedIp().substring(0,3);
                switch (code){
                    case "001": {
                        receiverCountry= "IND";
                        break;
                    }
                    case "002": {
                        receiverCountry= "USA";
                        break;
                    }
                    case "003": {
                        receiverCountry= "AUS";
                        break;
                    }
                    case "004": {
                        receiverCountry= "CHI";
                        break;
                    }
                    case "005": receiverCountry= "JPN";

                }
            }else{
                receiverCountry=receiver.getOriginalCountry().toString();
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
        return null;
    }
}
