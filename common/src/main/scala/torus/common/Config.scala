package torus.common

import java.io.InputStreamReader

import com.typesafe.config.ConfigFactory


object Config {

  val config = getClass.getResourceAsStream("/torus.conf")
  val parsedConfig = ConfigFactory.parseReader(new InputStreamReader(config))
  val plet = ConfigFactory.load(parsedConfig)

}
