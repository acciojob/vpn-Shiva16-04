package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin =new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Optional<Admin>optionalAdmin = adminRepository1.findById(adminId);
        Admin admin=optionalAdmin.get();
        ServiceProvider serviceProvider=new ServiceProvider();
        serviceProvider.setName(providerName);
        //initializing the arraylists
        serviceProvider.setUsers(new ArrayList<>());
        serviceProvider.setConnectionList(new ArrayList<>());
        serviceProvider.setCountryList(new ArrayList<>());
        //setting the foreign key
        serviceProvider.setAdmin(admin);
        //mapping bidirectionally
        admin.getServiceProviders().add(serviceProvider);
        adminRepository1.save(admin);
        //mapping bidirectionally
        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        //checking country name is subset of country enum or not
        String givenCountryName=countryName.toUpperCase();
        Country country=null;
        for(CountryName countryNameValue: CountryName.values()){
            if(countryNameValue.toString().equals(givenCountryName)){
                country=new Country();
                country.setCountryName(countryNameValue);
                country.setCode(countryNameValue.toCode());
            }
        }
        if(country==null){
            throw new Exception("Country not found");
        }
        Optional<ServiceProvider>optionalServiceProvider=serviceProviderRepository1.findById(serviceProviderId);
        ServiceProvider serviceProvider=optionalServiceProvider.get();
        //setting foreign key of the country;
        country.setServiceProvider(serviceProvider);
        //bidirectionally mapping the country
        serviceProvider.getCountryList().add(country);

        serviceProviderRepository1.save(serviceProvider);
        return serviceProvider;
    }
}
