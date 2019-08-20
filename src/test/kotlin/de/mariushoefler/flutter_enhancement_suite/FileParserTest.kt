package de.mariushoefler.flutter_enhancement_suite

import de.mariushoefler.flutter_enhancement_suite.utils.isPubPackageName
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FileParserTest {

	@Test
	fun packageNameContainsNameAndThreeNumbers() {
		assertTrue("test:1.0.0".isPubPackageName())
		assertFalse(":1.0.0".isPubPackageName())
		assertFalse("test".isPubPackageName())
		assertFalse("description: Flutter package".isPubPackageName())
	}

	@Test
	fun packageNameContainsSuffix() {
		assertTrue("test:1.0.0+4".isPubPackageName())
		assertTrue("test:1.0.0+hotfix.oopsie".isPubPackageName())
		assertTrue("test:1.0.0-alpha.12".isPubPackageName())
	}

	@Test
	fun packageNameDoesntContainVersionOrSdk() {
		assertFalse("version:1.0.0".isPubPackageName())
		assertFalse("sdk: '>=2.0.0 <3.0.0'".isPubPackageName())
	}
}