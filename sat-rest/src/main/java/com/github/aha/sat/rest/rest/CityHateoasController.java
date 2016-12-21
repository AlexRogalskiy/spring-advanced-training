package com.github.aha.sat.rest.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.aha.sat.rest.domain.City;
import com.github.aha.sat.rest.rest.hateoas.CityResource;
import com.github.aha.sat.rest.rest.hateoas.CityResourceAssembler;
import com.github.aha.sat.rest.rest.hateoas.SimpleCityResource;
import com.github.aha.sat.rest.rest.hateoas.SimpleCityResourceAssembler;
import com.github.aha.sat.rest.service.CityService;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * https://github.com/spring-projects/spring-hateoas http://java.dzone.com/articles/spring-mvc-and-hateoas
 * http://stateless.co/hal_specification.html
 */
@RestController
@RequestMapping("/city/resources")
public class CityHateoasController {

    @Autowired
    private CityService cityService;

	@Autowired
	private CityResourceAssembler assembler;

	@Autowired
	private SimpleCityResourceAssembler simpleAssembler;

    /*
	 * http://localhost:8080/city/resources/ http://localhost:8080/city/resources/?country=Spain, http://localhost:8080/city/resources/?sorting=id
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = { "application/hal+json" })
	public Resources<SimpleCityResource> list(
			@ApiParam(name = "country", required = false) @PathParam("country") String country,
            @ApiParam(name = "sorting", required = false) @PathParam("sorting") String sorting) {
		List<City> data = cityService.list(country, sorting);

		List<SimpleCityResource> resources = simpleAssembler.toResources(data);
		return new Resources<SimpleCityResource>(resources, linkTo(CityHateoasController.class).withSelfRel());
    }

	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = { "application/hal+json" })
	public Resources<CityResource> listAll(
			@ApiParam(name = "country", required = false) @PathParam("country") String country,
			@ApiParam(name = "sorting", required = false) @PathParam("sorting") String sorting) {
		List<City> data = cityService.list(country, sorting);

		List<CityResource> resources = assembler.toResources(data);
		return new Resources<CityResource>(resources, linkTo(CityHateoasController.class).withSelfRel());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { "application/hal+json" })
	public CityResource item(@PathVariable("id") long id) {
		// retrieve data
        City city = cityService.item(id);
        if (city == null) {
            throw new CityNotFoundException(String.format("City [id=%d] was not found!", id));
        }
		// prepare resource
		return assembler.toResource(city);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable long id) {
		cityService.delete(id);
	}

    @ResponseStatus(HttpStatus.NOT_FOUND)
    class CityNotFoundException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public CityNotFoundException(String message) {
            super(message);
        }
    }

}