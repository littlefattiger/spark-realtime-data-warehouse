package com.atguigu.gmall.realtime.util

import java.util.ResourceBundle

object MyPropsUtils {

  private val bundle: ResourceBundle = ResourceBundle.getBundle("config")

  def apply(propsKey: String) : String ={
    bundle.getString(propsKey)
  }

  def main(args: Array[String]): Unit = {
    println(MyPropsUtils("kafka.bootstrap-servers"))
  }
}
