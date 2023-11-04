package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        //checking country name is subset of country enum or not
        String givenCountryName=countryName.toUpperCase();
        Country country=new Country();;
        for(CountryName countryNameValue: CountryName.values()){
            if(countryNameValue.toString().equals(givenCountryName)){

                country.setCountryName(countryNameValue);
                country.setCode(countryNameValue.toCode());

                User user=new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setConnected(false);
                user.setMaskedIp(null);

                //initializing the foreign keys
                user.setConnectionList(new ArrayList<>());
                user.setServiceProviderList(new ArrayList<>());

                //setting the foreign key of country
                country.setUser(user);

                user.setOriginalIp(""+country.getCountryName().toCode()+"."+userRepository3.save(user).getId());

                userRepository3.save(user);
                return user;
            }
        }

        throw new Exception("Country not found");

    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        Optional<User>optionalUser=userRepository3.findById(userId);
        Optional<ServiceProvider>optionalServiceProvider=serviceProviderRepository3.findById(serviceProviderId);

        User user=optionalUser.get();
        ServiceProvider serviceProvider=optionalServiceProvider.get();

        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);

        userRepository3.save(user);
        return user;

    }
}
