package com.bmt.webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.data.domain.Sort;
import jakarta.validation.Valid;


import com.bmt.webapp.repositories.ClientRepository;
import com.bmt.webapp.models.ClientDto;
import com.bmt.webapp.models.Client;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepo;

    // Get list of clients
    @GetMapping({"", "/"})
    public String getClients(Model model) {
        try {
            var clients = clientRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
            model.addAttribute("clients", clients); // Fixed attribute name
            return "clients/index";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // Show the create client form
    @GetMapping("/create")
    public String createClient(Model model) {
        model.addAttribute("clientDto", new ClientDto());
        return "clients/create";
    }

    // Process form submission
    @PostMapping("/create")
    public String createClient(
        @Valid @ModelAttribute("clientDto") ClientDto clientDto,
        BindingResult result
    ) {
        // Check if email already exists
        if (clientRepo.findByEmail(clientDto.getEmail()) != null) {
            result.addError(
                new FieldError("clientDto", "email", clientDto.getEmail(),
                    false, null, null, "Email address is already used")
            );
        }

        // If there are validation errors, return to the form
        if (result.hasErrors()) {
            return "clients/create";
        }

        // Convert DTO to entity and save it
        Client client = new Client();
        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmail());
        client.setPhone(clientDto.getPhone());
        client.setAddress(clientDto.getAddress());
        client.setStatus(clientDto.getStatus());
       

        clientRepo.save(client);

        return "redirect:/clients";
    }
    
    
    
    
    @GetMapping("/edit")
    public String editClient(Model model, @RequestParam int id ) {
    	Client client = clientRepo.findById(id).orElse(null);
    	if(client == null) {
    		return"redirect:/clients";
    	}
    	
    	ClientDto clientDto = new ClientDto();
    	clientDto.setFirstName(client.getFirstName());
    	clientDto.setLastName(client.getLastName());
    	clientDto.setEmail(client.getEmail());
    	clientDto.setPhone(client.getPhone());
    	clientDto.setAddress(client.getAddress());
    	clientDto.setStatus(client.getStatus());

    	model.addAttribute("client", client);
    	model.addAttribute("clientDto", clientDto);

    	return "clients/edit";

    
    }
    
    
    @PostMapping("/edit")
    public String editClient(
        Model model,
        @RequestParam int id,
        @Valid @ModelAttribute("clientDto") ClientDto clientDto,
        BindingResult result
    ) {
        Client client = clientRepo.findById(id).orElse(null);
        if (client == null) {
            return "redirect:/clients";
        }

        if (result.hasErrors()) {
            model.addAttribute("client", client);
            return "clients/edit";
        }


        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmail());
        client.setPhone(clientDto.getPhone());
        client.setAddress(clientDto.getAddress());
        client.setStatus(clientDto.getStatus());

        clientRepo.save(client);


        
        
        try {
        	clientRepo.save(client);
        }
        catch(Exception ex) {
        	result.addError(
        			new FieldError("clientDto","email",clientDto.getEmail(), false, null, null, "Email adress already used"));
        	
        	return"clients/edit";
        }
    
        return "redirect:/clients";
	}
    
    @GetMapping("/delete")
    public String deleteClient(@RequestParam int id) {
        Client client = clientRepo.findById(id).orElse(null);

        if (client != null) {
            clientRepo.delete(client);
        }

        return "redirect:/clients";
    }

}