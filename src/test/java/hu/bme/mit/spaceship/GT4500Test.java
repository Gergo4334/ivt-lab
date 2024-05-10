package hu.bme.mit.spaceship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class GT4500Test {

  private GT4500 ship;
  private TorpedoStore primaryTorpedoStoreMock;
  private TorpedoStore secondaryTorpedoStoreMock;

  @BeforeEach
  public void init() {
    primaryTorpedoStoreMock = mock(TorpedoStore.class);
    secondaryTorpedoStoreMock = mock(TorpedoStore.class);
    this.ship = new GT4500(primaryTorpedoStoreMock, secondaryTorpedoStoreMock);
  }

  @Test
  public void fireTorpedo_Single_Success() {
    // Arrange
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(primaryTorpedoStoreMock.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertEquals(true, result);
    verify(primaryTorpedoStoreMock, times(1)).isEmpty();
    verify(primaryTorpedoStoreMock, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_PrimaryEmpty_SecondrayAvailable_Success() {
    // Arrange
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(true);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStoreMock.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertTrue(result);
    verify(primaryTorpedoStoreMock).isEmpty();
    verify(secondaryTorpedoStoreMock).isEmpty();
    verify(secondaryTorpedoStoreMock).fire(1);
    verify(primaryTorpedoStoreMock, never()).fire(1);
  }

  @Test
  public void fireTorpedo_SecondaryEmpty_PrimaryAvailable_Success() {
    // Arrange
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(true);
    when(primaryTorpedoStoreMock.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertTrue(result);
    verify(primaryTorpedoStoreMock).isEmpty();
    verify(primaryTorpedoStoreMock).fire(1);
    verify(secondaryTorpedoStoreMock, never()).fire(1);
  }

  @Test
  public void fireTorpedo_BothEmpty_Failure() {
    // Arrange
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(true);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertFalse(result);
    verify(primaryTorpedoStoreMock, times(1)).isEmpty();
    verify(secondaryTorpedoStoreMock, times(1)).isEmpty();
    verify(primaryTorpedoStoreMock, never()).fire(1);
    verify(secondaryTorpedoStoreMock, never()).fire(1);
  }

  @Test
  public void fireTorpedo_SecondaryEmpty_FirstFiredLast_Success() {
    // Arrange
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(true);

    // Ha az előző lövést az elsődleges torpedótárolóból adtuk le
    ship.fireTorpedo(FiringMode.SINGLE);
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(primaryTorpedoStoreMock.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertTrue(result);
    verify(primaryTorpedoStoreMock, times(2)).isEmpty();
    verify(secondaryTorpedoStoreMock, times(1)).isEmpty();
    verify(secondaryTorpedoStoreMock, never()).fire(1);
  }

  @Test
  public void fireTorpedo_SecondaryFirst_PrimaryFiredLast_Success() {
    // Arrange
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(false);

    // Ha az előző lövést az elsődleges torpedótárolóból adtuk le
    ship.fireTorpedo(FiringMode.SINGLE);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStoreMock.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertTrue(result);
    verify(secondaryTorpedoStoreMock, times(1)).isEmpty();
    verify(secondaryTorpedoStoreMock, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_SecondaryEmpty_PrimaryFiredLast_TryPrimaryAgain_Success() {
    // Arrange
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(true);
    when(primaryTorpedoStoreMock.fire(1)).thenReturn(true);

    // Ha az előző lövést az elsődleges torpedótárolóból adtuk le
    ship.fireTorpedo(FiringMode.SINGLE);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertTrue(result);
    verify(primaryTorpedoStoreMock, times(2)).isEmpty();
    verify(primaryTorpedoStoreMock, times(2)).fire(1);
    verify(secondaryTorpedoStoreMock, never()).fire(1);
  }

  @Test
  public void fireTorpedo_SecondaryEmpty_PrimaryFiredLast_TryPrimaryAgain_Failure() {
    // Arrange
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(true);
    when(primaryTorpedoStoreMock.fire(1)).thenReturn(true);

    // Ha az előző lövést az elsődleges torpedótárolóból adtuk le
    ship.fireTorpedo(FiringMode.SINGLE);
    
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertFalse(result);
    verify(primaryTorpedoStoreMock, times(2)).isEmpty();
    verify(primaryTorpedoStoreMock, times(1)).fire(1);
    verify(secondaryTorpedoStoreMock, never()).fire(1);
  }

  @Test
  public void fireTorpedo_All_Success() {
    // Arrange
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(primaryTorpedoStoreMock.fire(1)).thenReturn(true);
    when(secondaryTorpedoStoreMock.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertEquals(true, result);
    verify(primaryTorpedoStoreMock, times(1)).isEmpty();
    verify(secondaryTorpedoStoreMock, times(1)).isEmpty();
    verify(primaryTorpedoStoreMock, times(1)).fire(1);
    verify(secondaryTorpedoStoreMock, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_All_PrimaryEmpty_SecondaryAvailable_Failure() {
    // Arrange
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(true);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(primaryTorpedoStoreMock.fire(1)).thenReturn(true);
    when(secondaryTorpedoStoreMock.fire(1)).thenReturn(false);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertFalse(result);
    verify(primaryTorpedoStoreMock, times(1)).isEmpty();
    verify(primaryTorpedoStoreMock, never()).fire(1);
    verify(secondaryTorpedoStoreMock, never()).fire(1);
  }

  @Test
  public void fireTorpedo_All_SecondaryEmpty_PrimaryAvailable_Failure(){
    // Arrange
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(true);
    when(primaryTorpedoStoreMock.fire(1)).thenReturn(false);
    when(secondaryTorpedoStoreMock.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertFalse(result);
    verify(primaryTorpedoStoreMock, times(1)).isEmpty();
    verify(secondaryTorpedoStoreMock, times(1)).isEmpty();
    verify(primaryTorpedoStoreMock, never()).fire(1);
    verify(secondaryTorpedoStoreMock, never()).fire(1);
  }

  @Test
  public void fireTorpedo_All_BothEmpty_Failure(){
    // Arrenge
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(true);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertFalse(result);
    verify(primaryTorpedoStoreMock, times(1)).isEmpty();
    verify(primaryTorpedoStoreMock, never()).fire(1);
    verify(secondaryTorpedoStoreMock, never()).fire(1);
  }

  @Test
  public void fireTorpedo_All_PrimarySuccess_SecondaryFailure_Failure(){
    // Arrenge
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(primaryTorpedoStoreMock.fire(1)).thenReturn(true);
    when(secondaryTorpedoStoreMock.fire(1)).thenReturn(false);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertFalse(result);
    verify(primaryTorpedoStoreMock, times(1)).isEmpty();
    verify(primaryTorpedoStoreMock, times(1)).fire(1);
    verify(secondaryTorpedoStoreMock, times(1)).isEmpty();
    verify(secondaryTorpedoStoreMock, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_All_SecondarySuccess_PrimaryFailure_Failure(){
    // Arrenge
    when(primaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStoreMock.isEmpty()).thenReturn(false);
    when(primaryTorpedoStoreMock.fire(1)).thenReturn(false);
    when(secondaryTorpedoStoreMock.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertFalse(result);
    verify(primaryTorpedoStoreMock, times(1)).isEmpty();
    verify(primaryTorpedoStoreMock, times(1)).fire(1);
    verify(secondaryTorpedoStoreMock, times(1)).isEmpty();
    verify(secondaryTorpedoStoreMock, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_NonexistentFiringMode_Faliure(){
    // Act
    boolean result = ship.fireTorpedo(FiringMode.NONE);

    // Assert
    assertFalse(result);
  
  }

  @Test
  public void fireLaser_NotImplemented(){
    // Act
    boolean result = ship.fireLaser(FiringMode.SINGLE);

    // Assert
    assertFalse(result);
  }
}