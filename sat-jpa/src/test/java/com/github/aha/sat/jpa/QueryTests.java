package com.github.aha.sat.jpa;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.github.aha.sat.jpa.domain.City;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryTests extends AbstractCityTests {

    @Test
    public void testCount() {
        long count = cityRepository.count();
        assertThat(count, equalTo(15L));
    }

    @Test
    public void testPaging() {
		Page<City> page = cityRepository.findAll(PageRequest.of(0, 5));
        assertThat(page.getSize(), equalTo(5));
        assertThat(page.getTotalElements(), equalTo(15L));
        assertThat(page.getTotalPages(), equalTo(3));
		log.debug("\n### testPagingt output");
        for (City city : page.getContent()) {
			log.debug(city.toString());
        }
    }

    @Test
    public void testSorting() {
		Page<City> page = cityRepository.findAll(PageRequest.of(0, 5, Sort.Direction.DESC, "country", "name"));
        assertThat(page.getSize(), equalTo(5));
		log.debug("\n### testSorting output");
        for (City city : page.getContent()) {
			log.debug(city.toString());
        }
    }

    @Test
    public void testCityByName() {
        City city = cityRepository.findByNameAndCountryAllIgnoringCase("Tokyo", "Japan");
        assertThat(city.getName(), equalTo("Tokyo"));
        assertThat(city.getCountry(), equalTo("Japan"));
    }

}
