/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gentracklets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.util.FastMath;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.ZipJarCrawler;
import org.orekit.errors.OrekitException;
import org.orekit.forces.ForceModel;
import org.orekit.forces.SphericalSpacecraft;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.forces.radiation.SolarRadiationPressure;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.DateTimeComponents;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinatesProvider;

/**
 * Propagate objects to investigate the effects of different perturbing forces
 * on different orbit regimes
 *
 * @author zittersteijn
 */
public class Propagate {

    public static final DateTimeComponents YEAR = new DateTimeComponents(2016, 1, 1, 0, 0, 0.0);

    public static void main(String[] args) throws OrekitException {

        // load the data files
        File data = new File("/home/zittersteijn/Documents/java/libraries/orekit-data.zip");
        DataProvidersManager DM = DataProvidersManager.getInstance();
        ZipJarCrawler crawler = new ZipJarCrawler(data);
        DM.clearProviders();
        DM.addProvider(crawler);

        // Read in TLE elements
        File tleFile = new File("/home/zittersteijn/Documents/TLEs/ASTRA20151207.tle");
        FileReader TLEfr;
        Vector<TLE> tles = new Vector<>();
        tles.setSize(30);

        try {
            // read and save TLEs to a vector
            TLEfr = new FileReader("/home/zittersteijn/Documents/TLEs/ASTRA20151207.tle");
            BufferedReader readTLE = new BufferedReader(TLEfr);

            Scanner s = new Scanner(tleFile);

            String line1, line2;
            TLE2 tle = new TLE2();

            int nrOfObj = 4;
            for (int ii = 1; ii < nrOfObj + 1; ii++) {
                System.out.println(ii);
                line1 = s.nextLine();
                line2 = s.nextLine();
                if (TLE.isFormatOK(line1, line2)) {
                    tles.setElementAt(new TLE(line1, line2), ii);
                    System.out.println(tles.get(ii).toString());
                } else {
                    System.out.println("format problem");
                }

            }
            readTLE.close();

            // define a groundstation
            Frame inertialFrame = FramesFactory.getEME2000();
            TimeScale utc = TimeScalesFactory.getUTC();
            double longitude = FastMath.toRadians(7.465);
            double latitude = FastMath.toRadians(46.87);
            double altitude = 950.;
            GeodeticPoint station = new GeodeticPoint(latitude, longitude, altitude);
            Frame earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
            BodyShape earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS, Constants.WGS84_EARTH_FLATTENING, earthFrame);
            TopocentricFrame staF = new TopocentricFrame(earth, station, "station");

            Vector<Orbit> eles = new Vector<>();
            eles.setSize(tles.size());
            for (int ii = 1; ii < nrOfObj + 1; ii++) {
                double a = FastMath.pow(Constants.WGS84_EARTH_MU / FastMath.pow(tles.get(ii).getMeanMotion(), 2), (1.0 / 3));
                // convert them to orbits
                Orbit kep = new KeplerianOrbit(a,
                        tles.get(ii).getE(),
                        tles.get(ii).getI(),
                        tles.get(ii).getPerigeeArgument(),
                        tles.get(ii).getRaan(),
                        tles.get(ii).getMeanAnomaly(),
                        PositionAngle.MEAN,
                        inertialFrame,
                        tles.get(ii).getDate(),
                        Constants.WGS84_EARTH_MU);

                eles.setElementAt(kep, ii);

                // set up propagators
                KeplerianPropagator kepler = new KeplerianPropagator(eles.get(ii));

                System.out.println("a: " + a);

                // Initial state definition
                double mass = 1000.0;
                SpacecraftState initialState = new SpacecraftState(kep, mass);

                // Adaptive step integrator
                // with a minimum step of 0.001 and a maximum step of 1000
                double minStep = 0.001;
                double maxstep = 1000.0;
                double positionTolerance = 10.0;
                OrbitType propagationType = OrbitType.KEPLERIAN;
                double[][] tolerances
                        = NumericalPropagator.tolerances(positionTolerance, kep, propagationType);
                AdaptiveStepsizeIntegrator integrator
                        = new DormandPrince853Integrator(minStep, maxstep, tolerances[0], tolerances[1]);

                NumericalPropagator propagator = new NumericalPropagator(integrator);
                propagator.setOrbitType(propagationType);

                // set up and add force models
                double AMR = 4.0;
                double crossSection = mass * AMR;
                double Cd = 0.01;
                double Cr = 0.5;
                double Co = 0.8;
                NormalizedSphericalHarmonicsProvider provider = GravityFieldFactory.getNormalizedProvider(4, 4);
                ForceModel holmesFeatherstone = new HolmesFeatherstoneAttractionModel(FramesFactory.getITRF(IERSConventions.IERS_2010, true), provider);
                SphericalSpacecraft ssc = new SphericalSpacecraft(crossSection, Cd, Cr, Co);
                PVCoordinatesProvider sun = CelestialBodyFactory.getSun();
                SolarRadiationPressure srp = new SolarRadiationPressure(sun, Constants.WGS84_EARTH_EQUATORIAL_RADIUS, ssc);

//                propagator.addForceModel(srp);
//                propagator.addForceModel(holmesFeatherstone);
                propagator.setInitialState(initialState);

                // propagate the orbits with steps size and tracklet lenght at several epochs (tracklets)
                Vector<AbsoluteDate> startDates = new Vector<>();
                startDates.setSize(1);
                startDates.setElementAt(new AbsoluteDate(2016, 1, 26, 20, 00, 00, utc), 0);

                // set the step size [s] and total length
                double tstep = 100;
                double ld = 3;
                double ls = FastMath.floor(ld * (24 * 60 * 60) / tstep);
                System.out.println(ls);

                SpacecraftState currentStateKep = kepler.propagate(startDates.get(0));
                SpacecraftState currentStatePer = propagator.propagate(startDates.get(0));

                for (int tt = 0; tt < startDates.size(); tt++) {

                    // set up output file
                    String app = tles.get(ii).getSatelliteNumber() + "_" + startDates.get(tt) + ".txt";

                    // with formatted output
                    File file1 = new File("/home/zittersteijn/Documents/propagate/keplerian/MEO/" + app);
                    File file2 = new File("/home/zittersteijn/Documents/propagate/perturbed/MEO/" + app);
                    file1.createNewFile();
                    file2.createNewFile();
                    Formatter fmt1 = new Formatter(file1);
                    Formatter fmt2 = new Formatter(file2);

                    for (int kk = 0; kk < (int) ls; kk++) {
                        AbsoluteDate propDate = startDates.get(tt).shiftedBy(tstep * kk);
                        currentStateKep = kepler.propagate(propDate);
                        currentStatePer = propagator.propagate(propDate);

                        System.out.println(currentStateKep.getPVCoordinates().getPosition() + "\t" + currentStateKep.getDate());

                        // convert to RADEC coordinates
                        double[] radecKep = conversions.geo2radec(currentStateKep.getPVCoordinates(), staF, inertialFrame, propDate);
                        double[] radecPer = conversions.geo2radec(currentStatePer.getPVCoordinates(), staF, inertialFrame, propDate);

                        // write the orbit to seperate files with the RA, DEC, epoch and fence given
                        AbsoluteDate year = new AbsoluteDate(YEAR, utc);
                        fmt1.format("%.12f %.12f %.12f %d%n", radecKep[0], radecKep[2], (currentStateKep.getDate().durationFrom(year) / (24 * 3600)), (tt + 1));
                        fmt2.format("%.12f %.12f %.12f %d%n", radecPer[0], radecPer[2], (currentStateKep.getDate().durationFrom(year) / (24 * 3600)), (tt + 1));

                    }
                    fmt1.flush();
                    fmt1.close();
                    fmt2.flush();
                    fmt2.close();

                }
                double[] radecKep = conversions.geo2radec(currentStateKep.getPVCoordinates(), staF, inertialFrame, new AbsoluteDate(startDates.get(0), ls * tstep));
                double[] radecPer = conversions.geo2radec(currentStatePer.getPVCoordinates(), staF, inertialFrame, new AbsoluteDate(startDates.get(0), ls * tstep));
                double sig0 = 1.0 / 3600.0 / 180.0 * FastMath.PI;
                double dRA = radecKep[0] - radecPer[0] / (sig0 * sig0);
                double dDEC = radecKep[2] - radecPer[2] / (sig0 * sig0);
                
                System.out.println(dRA + "\t" + dDEC);

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenTracklets.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException iox) {
            Logger.getLogger(GenTracklets.class.getName()).log(Level.SEVERE, null, iox);
        }

    }

}
