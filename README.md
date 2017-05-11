This is a Java tag library with CMS components for the Google App Engine.

Installation
============

This package is available on Maven Central

    <groupId>de.hepisec.taglib.cms</groupId>
    <artifactId>gaecms-taglib</artifactId>
    <version>1.0</version>

To copy the required JavaScript and CSS files to your .war include the following code in the build section of your pom.xml

    <plugin>
        <artifactId>maven-remote-resources-plugin</artifactId>
        <version>1.5</version>
        <executions>
            <execution>
                <goals>
                    <goal>process</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <resourceBundles>
                <resourceBundle>de.hepisec.taglib.cms:gaecms-taglib:1.0</resourceBundle>
            </resourceBundles>
        </configuration>
    </plugin>                                    
    <plugin> 
        <groupId>org.apache.maven.plugins</groupId> 
        <artifactId>maven-war-plugin</artifactId>
        <version>3.1.0</version> 
        <configuration> 
            <webResources> 
                <resource> 
                    <directory>${project.build.directory}/maven-shared-archive-resources/webapp</directory> 
                    <includes> 
                        <include>**/*</include> 
                    </includes> 
                </resource> 
            </webResources> 
        </configuration> 
    </plugin>             


Changelog
=========

1.0 - Initial Release
