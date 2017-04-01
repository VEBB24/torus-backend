package torus.common

import java.io.InputStreamReader

import com.typesafe.config.ConfigFactory


object CommonConfig {

  val config = getClass.getResourceAsStream("/torus.conf")
  val parsedConfig = ConfigFactory.parseReader(new InputStreamReader(config))
  val torus = ConfigFactory.load(parsedConfig)

}
