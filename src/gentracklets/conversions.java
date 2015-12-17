/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gentracklets;

import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.FastMath;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.TopocentricFrame;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.DateComponents;
import org.orekit.time.TimeComponents;
import org.orekit.time.TimeScale;
import org.orekit.utils.PVCoordinates;
import org.apache.commons.math3.stat.regression.*;
import org.orekit.orbits.CartesianOrbit;
import org.orekit.utils.Constants;

/**
 *
 * @author zittersteijn
 */
public class conversions {

  
    public static double[] geo2radec(PVCoordinates obj, TopocentricFrame staF, Frame inertialFrame, AbsoluteDate epoch) {

        Vector3D rho = new Vector3D(0, 0, 0);

        try {
            rho = obj.getPosition().subtract(staF.getPVCoordinates(epoch, inertialFrame).getPosition());
        } catch (OrekitException ex) {
            Logger.getLogger(conversions.class.getName()).log(Level.SEVERE, null, ex);
        }

        double rho_mag = rho.getNorm();
        double DEC = FastMath.asin(rho.getZ() / rho_mag);
        double cosRA = 0.0;
        double sinRA = 0.0;
        double RA = 0.0;

        Vector3D v_site = new Vector3D(0, 0, 0);
        try {
            v_site = staF.getPVCoordinates(epoch, inertialFrame).getVelocity();
        } catch (OrekitException ex) {
            Logger.getLogger(conversions.class.getName()).log(Level.SEVERE, null, ex);
        }

        Vector3D rhoDot = obj.getVelocity().subtract(v_site);

        if (FastMath.sqrt(FastMath.pow(rho.getX(), 2) + FastMath.pow(rho.getY(), 2)) != 0) {
            cosRA = rho.getX() / FastMath.sqrt(FastMath.pow(rho.getX(), 2) + FastMath.pow(rho.getY(), 2));
            sinRA = rho.getY() / FastMath.sqrt(FastMath.pow(rho.getX(), 2) + FastMath.pow(rho.getY(), 2));
            RA = FastMath.atan2(sinRA, cosRA);
            if (RA <= 0) {
                RA = RA + 2 * FastMath.PI;
            }
        } else {
            sinRA = rhoDot.getY() / FastMath.sqrt(FastMath.pow(rhoDot.getX(), 2) + FastMath.pow(rhoDot.getY(), 2));
            cosRA = rhoDot.getX() / FastMath.sqrt(FastMath.pow(rhoDot.getX(), 2) + FastMath.pow(rhoDot.getY(), 2));
            RA = FastMath.atan2(sinRA, cosRA);
            if (RA <= 0) {
                RA = RA + 2 * FastMath.PI;
            }
        }

        double rhoDot_mag = rho.dotProduct(rhoDot) / rho_mag;
        double RAdot = (rhoDot.getX() * rho.getY() - rhoDot.getY() * rho.getX()) / (-1 * FastMath.pow(rho.getY(), 2) - FastMath.pow(rho.getX(), 2));
        double DECdot = (rhoDot.getZ() - rhoDot_mag * FastMath.sin(DEC)) / FastMath.sqrt(FastMath.pow(rho.getX(), 2) + FastMath.pow(rho.getY(), 2));

        double[] out = {RA, RAdot, DEC, DECdot, rho_mag, rhoDot_mag};

        return out;
    }

    
}
