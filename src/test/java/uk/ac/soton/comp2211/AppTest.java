package uk.ac.soton.comp2211;

import org.junit.Ignore;
import org.junit.Test;
import uk.ac.soton.comp2211.controller.listeners.CreationListener;
import uk.ac.soton.comp2211.controller.listeners.SetListener;
import uk.ac.soton.comp2211.model.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

/** Unit testing Testing framework: JUnit 5 */
public class AppTest {

  /** Basic information */
  Obstacle obstacle1;
  Obstacle obstacle2;
  Obstacle obstacle3;
  Obstacle obstacle5;
  Obstacle obstacle6;
  Runway uniform;

  public AppTest() {
    try {
      obstacle1 = new Obstacle("Standing bus", 12, 3, 3);
      obstacle2 = new Obstacle("Tower", 25, 2, 4);
      obstacle3 = new Obstacle("Pole", 15, 1, 1);
      obstacle5 = new Obstacle("Catering truck", 6,3,5);
      obstacle6 = new Obstacle("Large Box", 3, 2, 2);

      uniform = new Runway(9, 'C', 60, 240,
          2000, 2000, 2000, 2000, 0,
          2000, 2000, 2000, 2000, 0);
    } catch (Exception e) {
      fail("Setup failed: "+e.getMessage());
    }
  }

  /*
   * Tests will be done in order of class depth, ascending logical runways first, then runways, then master model
   */

  /** Scenario 1 Testing in lowest depth (logical) */
  @Test
  public void testS1Logical1() {
    LogicalRunway ohNineL = new LogicalRunway(240,9,'L',306,3902,3902,3902,3595);
    ohNineL.setObstacle(obstacle1, -50, 0);
    ohNineL.redeclare(50, 300, 60);
    assertEquals("new TORA",3346,ohNineL.getNewTORA());
    assertEquals("new TODA",3346,ohNineL.getNewTODA());
    assertEquals("new ASDA",3346,ohNineL.getNewASDA());
    assertEquals("new LDA",2985,ohNineL.getNewLDA());
  }

  @Test
  public void testS1Logical2() {
    LogicalRunway twoSevenR = new LogicalRunway(240,27,'R',0,3884,3962,3884,3884);
    twoSevenR.setObstacle(obstacle1, 3646, 0);
    twoSevenR.redeclare(50, 300, 60);
    assertEquals("new TORA",2986,twoSevenR.getNewTORA());
    assertEquals("new TODA",2986,twoSevenR.getNewTODA());
    assertEquals("new ASDA",2986,twoSevenR.getNewASDA());
    assertEquals("new LDA",3346,twoSevenR.getNewLDA());
  }

  /** Scenario 2 Testing in next lowest depth (runways) */
  @Test
  public void testS2Runway() {
    try {
      Runway runway_09R_27L = new Runway(9,'R',60,240,
          3660,3660,3660,3353,307,
          3660,3660,3660,3660,0);
      obstacle2.setdThreshold1(2853); obstacle2.setdThreshold2(500); obstacle2.setdCenter(20);
      runway_09R_27L.setObstacle(obstacle2);
      runway_09R_27L.redeclare(50, 300);
      LogicalRunway lr09R = runway_09R_27L.getLogicalRunway1();
      LogicalRunway lr27L = runway_09R_27L.getLogicalRunway2();

      assertEquals("09R TORA",1850,lr09R.getNewTORA());
      assertEquals("09R TODA",1850,lr09R.getNewTODA());
      assertEquals("09R ASDA",1850,lr09R.getNewASDA());
      assertEquals("09R LDA",2553,lr09R.getNewLDA());

      assertEquals("27L TORA",2860,lr27L.getNewTORA());
      assertEquals("27L TODA",2860,lr27L.getNewTODA());
      assertEquals("27L ASDA",2860,lr27L.getNewASDA());
      assertEquals("27L LDA",1850,lr27L.getNewLDA());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /** Scenario 3 Testing in next lowest depth (runways) */
  @Test
  public void testS3Runway() {
    try {
      Runway runway_09R_27L =
          new Runway(9,'R',60,240,
              3660,3660,3660,3353,307,
              3660,3660,3660,3660,0);
      obstacle3.setdThreshold1(150); obstacle3.setdThreshold2(3203); obstacle3.setdCenter(60);
      runway_09R_27L.setObstacle(obstacle3);
      runway_09R_27L.redeclare(50, 300);
      LogicalRunway lr09R = runway_09R_27L.getLogicalRunway1();
      LogicalRunway lr27L = runway_09R_27L.getLogicalRunway2();

      assertEquals("09R TORA",2903, lr09R.getNewTORA());
      assertEquals("09R TODA",2903, lr09R.getNewTODA());
      assertEquals("09R ASDA",2903, lr09R.getNewASDA());
      assertEquals("09R LDA",2393, lr09R.getNewLDA());

      assertEquals("27L TORA",2393, lr27L.getNewTORA());
      assertEquals("27L TODA",2393, lr27L.getNewTODA());
      assertEquals("27L ASDA",2393, lr27L.getNewASDA());
      assertEquals("27L LDA",2903, lr27L.getNewLDA());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /** Scenario 4 Testing in highest depth (master model) */
  @Test
  public void testS4MasterModel() {
    try {
      // Test setup
      SetListener<Airport> setAirport =
          new SetListener<>() {
            @Override
            public void setParameter(Airport airport) {}
          };
      SetListener<Runway> setRunway =
          new SetListener<>() {
            @Override
            public void setParameter(Runway runway) {}
          };
      SetListener<Obstacle> setObstacle =
          new SetListener<>() {
            @Override
            public void setParameter(Obstacle obstacle) {}
          };
      CreationListener<Airport> createAirport
          = new CreationListener<>() {
            @Override
            public void createParameter(Airport param) {}
          };
      CreationListener<Runway> createRunway
          = new CreationListener<>() {
            @Override
            public void createParameter(Runway param) {}
          };
      CreationListener<Obstacle> createObstacle
          = new CreationListener<>() {
            @Override
            public void createParameter(Obstacle param) {}
          };

      MasterModel model = new MasterModel();
      model.setSetAirportListener(setAirport);
      model.setSetRunwayListener(setRunway);
      model.setSetObstacleListener(setObstacle);
      model.setCreateAirportListener(createAirport);
      model.setCreateRunwayListener(createRunway);
      model.setCreateObstacleListener(createObstacle);

      ArrayList<Runway> runways = new ArrayList<>();
      Airport airport = new Airport("Heathrow", runways, 50, 300);
      Runway runway = new Runway(9,'L',60,240,
              3902,3902,3902,3595,306,
              3884,3962,3884,3884,0);
      Obstacle obstacle4 = new Obstacle("Tall Pole",20,1,1,3546,50,-20);

      model.createAirport(airport);
      model.createRunway(runway);
      model.createObstacle(obstacle4);
      Runway calculated = model.redeclare();
      LogicalRunway towards = calculated.getLogicalRunway1();
      LogicalRunway away = calculated.getLogicalRunway2();

      assertEquals("towards: Incorrect TORA",2792,towards.getNewTORA());
      assertEquals("towards: Incorrect TODA",2792,towards.getNewASDA());
      assertEquals("towards: Incorrect ASDA",2792,towards.getNewTODA());
      assertEquals("towards: Incorrect LDA",3246,towards.getNewLDA());

      assertEquals("away: Incorrect TORA",3534,away.getNewTORA());
      assertEquals("away: Incorrect TODA",3534,away.getNewASDA());
      assertEquals("away: Incorrect ASDA",3612,away.getNewTODA());
      assertEquals("away: Incorrect LDA",2774,away.getNewLDA());
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /** Testing that the runway should have original values when an obstacle is too far away from the runway ends
   * This is so a calculated TORA / LDA is not greater than the original values */
  @Test
  public void testOutOfBoundsObstacleEnd() {
    try {
      Runway runway = uniform;
      obstacle5.setdThreshold1(-450); obstacle5.setdThreshold2(2450); obstacle5.setdCenter(0);
      runway.setObstacle(obstacle5);
      runway.redeclare(50,300);
      LogicalRunway behind = runway.getLogicalRunway1(); LogicalRunway ahead = runway.getLogicalRunway2();

      assertEquals("Behind: New TORA greater than original",2000, behind.getNewTORA());
      assertEquals("Behind: New TODA greater than original",2000, behind.getNewTODA());
      assertEquals("Behind: New ASDA greater than original",2000, behind.getNewASDA());
      assertEquals("Behind: New LDA greater than original",2000, behind.getNewLDA());

      assertEquals("Ahead: New TORA greater than original",2000, ahead.getNewTORA());
      assertEquals("Ahead: New TODA greater than original",2000, ahead.getNewTODA());
      assertEquals("Ahead: New ASDA greater than original",2000, ahead.getNewASDA());
      assertEquals("Ahead: New LDA greater than original",2000, ahead.getNewLDA());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /** Testing that a runway should not be recalculated if an obstacle is too far from the runway to the side */
  @Ignore  // TODO: Out of bounds of clear/graded area (obstacle off to side of runway)
  public void testOutOfBoundsObstacleSide() {
    try {
      Runway runway = uniform;
      obstacle5.setdThreshold1(1000); obstacle5.setdThreshold2(1000); obstacle5.setdCenter(500);
      runway.setObstacle(obstacle5);
      runway.redeclare(50,300);
      LogicalRunway run1 = runway.getLogicalRunway1(); LogicalRunway run2 = runway.getLogicalRunway2();

      assertEquals("run1: Expected original TORA",2000, run1.getNewTORA());
      assertEquals("run1: Expected original TODA",2000, run1.getNewTODA());
      assertEquals("run1: Expected original ASDA",2000, run1.getNewASDA());
      assertEquals("run1: Expected original LDA",2000, run1.getNewLDA());

      assertEquals("run2: Expected original TORA",2000, run2.getNewTORA());
      assertEquals("run2: Expected original TODA",2000, run2.getNewTODA());
      assertEquals("run2: Expected original ASDA",2000, run2.getNewASDA());
      assertEquals("run2: Expected original LDA",2000, run2.getNewLDA());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /** Testing the redeclaration logic for an obstacle at the exact centre of a runway with uniform parameters and then
   * 10m towards one threshold. This test shows that the logic will try to maximise the parameters which is beneficial
   * to keep the runway open and give pilots more runway surface available.
   *  When in the middle, the LDA should be declared towards and the TORA, TODA and ASDA should be declared away from
   * the obstacle. Otherwise, when obstacle is moved 10m towards one threshold, one logical runway will land AND
   * take-off away and the other towards. */
  @Test
  public void testMiddleOfRunway1() {
    try {
      Runway runway = uniform;
      obstacle5.setdThreshold1(1000); obstacle5.setdThreshold2(1000); obstacle5.setdCenter(0);
      runway.setObstacle(obstacle5); runway.redeclare(50, 300);
      LogicalRunway run1 = runway.getLogicalRunway1();
      LogicalRunway run2 = runway.getLogicalRunway2();
      // Logical Runways do not have aircraft landing or taking off over the obstacle
      assertEquals("run1: TORA should be maximised, declared away", 700, run1.getNewTORA());
      assertEquals("run1: LDA should be maximised, declared towards", 700, run1.getNewLDA());
      assertEquals("run2: TORA should be maximised, declared away", 700, run2.getNewTORA());
      assertEquals("run2: LDA should be maximised, declared towards", 700, run2.getNewLDA());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
  @Test
  public void testMiddleOfRunway2() {
    try {
      Runway runway = uniform;
      obstacle5.setdThreshold1(990); obstacle5.setdThreshold2(1010); obstacle5.setdCenter(0);
      runway.setObstacle(obstacle5); runway.redeclare(50,300);
      LogicalRunway run1 = runway.getLogicalRunway1();
      LogicalRunway run2 = runway.getLogicalRunway2();
      // Logical Runways declare either both away or towards obstacle
      assertEquals("run1: TORA declared away",710,run1.getNewTORA());
      assertEquals("run1: LDA declared away",650,run1.getNewLDA());
      assertEquals("run2: TORA declared towards",650,run2.getNewTORA());
      assertEquals("run2: LDA declared away",710,run2.getNewLDA());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /** A funky edge case where the logical runway has a large threshold displacement and a short obstacle distance
   * so it is better for a plane to land over the obstacle but also take-off before it */
  @Test
  public void testLargeDisplacementEdgeCase() {
    try {
      Runway runway = new Runway(9,'C',60,240,
          2000,2000,2000,1100,900,
          2000,2000,2000,2000,0);
      obstacle6.setdThreshold1(300); obstacle6.setdThreshold2(800); obstacle6.setdCenter(0);
      runway.setObstacle(obstacle6); runway.redeclare(50,300);
      LogicalRunway testing = runway.getLogicalRunway1();

      assertEquals("LDA away",440,testing.getNewLDA());
      assertEquals("TORA towards",840,testing.getNewTORA());
      assertEquals("TODA towards",840,testing.getNewTODA());
      assertEquals("ASDA towards",840,testing.getNewASDA());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  // TODO: More tests

  /** INVALID PARAMETER SETUP - There are none for LogicalRunway as they are never defined outside Runway */
  @Test
  public void invalidRunway1() {
    try {
      Runway runway = new Runway(-1,'C',60,240,
          2000,2000,2000,2000,0,
          2000,2000,2000,2000,0);
      fail("Expected error to be thrown to negative heading");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway2() {
    try {
      Runway runway = new Runway(18,'C',60,240,
          2000,2000,2000,2000,0,
          2000,2000,2000,2000,0);
      fail("Expected error to be thrown to large heading");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway3() {
    try {
      Runway runway = new Runway(9,'C',-60,240,
          2000,2000,2000,2000,0,
          2000,2000,2000,2000,0);
      fail("Expected error to be thrown to negative strip end");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway4() {
    try {
      Runway runway = new Runway(9,'C',60,60,
          2000,2000,2000,2000,0,
          2000,2000,2000,2000,0);
      fail("Expected error to be thrown to unsafe RESA");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway5() {
    try {
      Runway runway = new Runway(9,'C',60,240,
          2000,2000,2000,2000,-10,
          2000,2000,2000,2000,0);
      fail("Expected error to be thrown to negative threshold displacement (1)");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway6() {
    try {
      Runway runway = new Runway(9,'C',60,240,
          2000,2000,2000,2000,0,
          2000,2000,2000,2000,-10);
      fail("Expected error to be thrown to negative threshold displacement (2)");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway7() {
    try {
      Runway runway = new Runway(9,'C',60,240,
          900,2000,2000,2000,0,
          2000,2000,2000,2000,0);
      fail("Expected error to be thrown to unsafe TORA (1)");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway8() {
    try {
      Runway runway = new Runway(9,'C',60,240,
          2000,2000,2000,2000,0,
          900,2000,2000,2000,0);
      fail("Expected error to be thrown to unsafe TORA (2)");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway9() {
    try {
      Runway runway = new Runway(9,'C',60,240,
          2000,900,2000,2000,0,
          2000,2000,2000,2000,0);
      fail("Expected error to be thrown to unsafe TODA (1)");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway10() {
    try {
      Runway runway = new Runway(9,'C',60,240,
          2000,2000,2000,2000,0,
          2000,900,2000,2000,0);
      fail("Expected error to be thrown to unsafe TODA (2)");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway11() {
    try {
      Runway runway = new Runway(9,'C',60,240,
          2000,2000,900,2000,0,
          2000,2000,2000,2000,0);
      fail("Expected error to be thrown to unsafe ASDA (1)");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway12() {
    try {
      Runway runway = new Runway(9,'C',60,240,
          2000,2000,2000,2000,0,
          2000,2000,900,2000,0);
      fail("Expected error to be thrown to unsafe ASDA (2)");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway13() {
    try {
      Runway runway = new Runway(9,'C',60,240,
          2000,2000,2000,900,0,
          2000,2000,2000,2000,0);
      fail("Expected error to be thrown to unsafe LDA (1)");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway14() {
    try {
      Runway runway = new Runway(9,'C',60,240,
          2000,2000,2000,2000,0,
          2000,2000,2000,900,0);
      fail("Expected error to be thrown to unsafe LDA (2)");
    } catch (Exception e) {
      assertEquals("Runway parameters are invalid",e.getMessage());
    }
  }
  @Test
  public void invalidRunway15() {
    try {
      Runway runway = new Runway(9,'C',60,240,
          2000,2000,2000,2000,0,
          2000,2000,2000,2000,0);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
  @Test
  public void invalidObstacle1() {
    try {
      Obstacle obstacle = new Obstacle("Antonov",-5,5,5);
      fail("Expected error to be thrown to negative height");
    } catch (Exception e) {
      assertEquals("An obstacle parameter was negative",e.getMessage());
    }
  }
  @Test
  public void invalidObstacle2() {
    try {
      Obstacle obstacle = new Obstacle("Antonov",5,-5,5);
      fail("Expected error to be thrown to negative width");
    } catch (Exception e) {
      assertEquals("An obstacle parameter was negative",e.getMessage());
    }
  }
  @Test
  public void invalidObstacle3() {
    try {
      Obstacle obstacle = new Obstacle("Antonov",5,5,-5);
      fail("Expected error to be thrown to negative length");
    } catch (Exception e) {
      assertEquals("An obstacle parameter was negative",e.getMessage());
    }
  }
  @Test
  public void invalidObstacle4() {
    try {
      Obstacle obstacle = new Obstacle("Antonov",5,5,5);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
  @Test
  public void invalidAirport1() {
    try {
      Airport airport = new Airport("Southampton",null,50,300);
      fail("Expected error to be thrown to null runway arraylist");
    } catch (Exception e) {
      assertEquals("Airport initialised with negative parameters or had a null runways list",e.getMessage());
    }
  }
  @Test
  public void invalidAirport2() {
    try {
      Airport airport = new Airport("Southampton",new ArrayList<>(),0,300);
      fail("Expected error to be thrown to invalid minimum ascent ratio");
    } catch (Exception e) {
      assertEquals("Airport initialised with negative parameters or had a null runways list",e.getMessage());
    }
  }
  @Test
  public void invalidAirport3() {
    try {
      Airport airport = new Airport("Southampton",new ArrayList<>(),50,-1);
      fail("Expected error to be thrown to negative blast distance");
    } catch (Exception e) {
      assertEquals("Airport initialised with negative parameters or had a null runways list",e.getMessage());
    }
  }
  @Test
  public void invalidAirport4() {
    try {
      Airport airport = new Airport("Southampton",new ArrayList<>(),50,300);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
  // I have gone insane

}
