/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.worker.drivers.chrome;

import static com.cognifide.aet.worker.drivers.WebDriverConstants.NAME;
import static com.cognifide.aet.worker.drivers.WebDriverConstants.NAME_LABEL;
import static com.cognifide.aet.worker.drivers.WebDriverConstants.PATH;

import com.cognifide.aet.job.api.collector.HttpRequestExecutorFactory;
import com.cognifide.aet.job.api.collector.ProxyServerWrapper;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.worker.api.WebDriverFactory;
import com.cognifide.aet.worker.exceptions.WorkerException;
import java.util.Map;
import java.util.logging.Level;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.osgi.framework.Constants;

@Service
@Component(immediate = false, description = "AET Chrome WebDriver Factory", label = "AET Chrome WebDriver Factory", metatype = true)
@Properties({@Property(name = Constants.SERVICE_VENDOR, value = "Cognifide Ltd")})
public class ChromeWebDriverFactory implements WebDriverFactory {

  private static final String DEFAULT_DRIVER_PATH = "/usr/bin/chromedriver";
  private static final String DEFAULT_BROWSER_NAME = "chrome";

  @Reference
  private HttpRequestExecutorFactory requestExecutorFactory;

  @Property(name = NAME, label = NAME_LABEL, value = DEFAULT_BROWSER_NAME)
  private String name;

  @Property(name = PATH, label = "Custom path to Chrome driver binary")
  private String path;

  @Override
  public String getName() {
    return name;
  }

  @Activate
  public void activate(Map<String, String> properties) {
    this.name = PropertiesUtil.toString(properties.get(NAME), DEFAULT_BROWSER_NAME);
    this.path = PropertiesUtil.toString(properties.get(PATH), DEFAULT_DRIVER_PATH);
  }

  @Override
  public WebCommunicationWrapper createWebDriver() throws WorkerException {
    try {
      final DesiredCapabilities capabilities = setupCapabilities();
      return new ChromeCommunicationWrapperImpl(new ChromeDriver(capabilities), null,
          requestExecutorFactory.createInstance());
    } catch (Exception e) {
      throw new WorkerException(e.getMessage(), e);
    }
  }

  @Override
  public WebCommunicationWrapper createWebDriver(ProxyServerWrapper proxyServer)
      throws WorkerException {
    try {
      Proxy proxy = proxyServer.seleniumProxy();
      proxyServer.setCaptureContent(true);
      proxyServer.setCaptureHeaders(true);

      final DesiredCapabilities capabilities = setupCapabilities();
      capabilities.setCapability(CapabilityType.PROXY, proxy);

      return new ChromeCommunicationWrapperImpl(new ChromeDriver(capabilities), proxyServer,
          requestExecutorFactory.createInstance());
    } catch (Exception e) {
      throw new WorkerException(e.getMessage(), e);
    }
  }

  private DesiredCapabilities setupCapabilities() {
    System.setProperty("webdriver.chrome.driver", path);
    final DesiredCapabilities capabilities = DesiredCapabilities.chrome();
    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

    final LoggingPreferences loggingPreferences = new LoggingPreferences();
    loggingPreferences.enable(LogType.BROWSER, Level.ALL);
    capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--disable-plugins");

    capabilities.setCapability(ChromeOptions.CAPABILITY, options);

    return capabilities;
  }
}
