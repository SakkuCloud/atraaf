/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.makbn.atraaf;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;



/**
 * The application main class.
 */

@SpringBootApplication(
        exclude = {RepositoryRestMvcAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class},
        scanBasePackages = "io.github.makbn")
public class Main {

    public static void main(final String[] args) {
        SpringApplication application = new SpringApplication();
        application.setLogStartupInfo(true);
        application.run(Main.class, args);
    }

}
