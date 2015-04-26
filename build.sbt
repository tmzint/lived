import android.Keys._

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

android.Plugin.androidBuildAar

name := "lived"


manifest in Android := <manifest package="com.tmzint.android.lived">
  <application/>
</manifest>

processManifest in Android := file("/")

rGenerator in Android := Nil

organization := "com.tmzint"

version := "0.1"

scalaVersion := "2.11.6"

platformTarget in Android := "android-16"

debugIncludesTests in Android := false
