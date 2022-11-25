package Assistant;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import star.amr.AmrModel;
import star.base.neo.DoubleVector;
import star.base.neo.NeoObjectVector;
import star.base.neo.NeoProperty;
import star.base.neo.StringVector;
import star.cadmodeler.*;
import star.combustion.*;
import star.combustion.fgm.FgmCombustionModel;
import star.combustion.fgm.FgmIdealGasModel;
import star.combustion.fgm.FgmReactionModel;
import star.combustion.fgm.FgmTable;
import star.combustion.tablegenerators.*;
import star.common.*;
import star.dualmesher.VolumeControlDualMesherSizeOption;
import star.emissions.NoxModel;
import star.emissions.NoxThreeEquationZeldovichModel;
import star.emissions.ThermalNoxModel;
import star.flow.MassFlowRateProfile;
import star.flow.VelocityMagnitudeProfile;
import star.keturb.KEpsilonTurbulence;
import star.keturb.KeTwoLayerAllYplusWallTreatment;
import star.keturb.RkeTwoLayerTurbModel;
import star.material.MaterialDataBaseManager;
import star.material.MultiComponentGasModel;
import star.meshing.*;
import star.reactions.ReactingModel;
import star.resurfacer.VolumeControlResurfacerSizeOption;
import star.segregatedenergy.SegregatedFluidEnthalpyModel;
import star.segregatedflow.SegregatedFlowModel;
import star.turbulence.RansTurbulenceModel;
import star.turbulence.TurbulentModel;
import star.vis.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;
import java.util.Properties;


import java.io.File;
import java.util.concurrent.TimeUnit;


public class simpleComplex extends StarMacro {

private static  String PYTHON_SCRIPT_PATH;
private static  String GRIMECH30_PATH;
private static  String THERMO30_PATH;
private static  String TRANSPORT_PATH;
private static int EXCEL_START_ROW;
private static int EXCEL_START_COLL;

    public void execute() {

        getConfigData("C:\\Users\\NULS\\Desktop\\folder\\config.ini");
        formChoose();
    }

    private void importGeometry() {

        Simulation simulation =
                getActiveSimulation();

        //open a file chooser
        // JFileChooser fileChooser = new JFileChooser();
        // fileChooser.setDialogTitle("Select a folder");
        // fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // String resultPath;
        // if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        //     resultPath = fileChooser.getSelectedFile().getPath();
        //     simulation.println(resultPath);
        // } else {
        //     simulation.println("Select the geometry file");
        //     return;
        // }

        CadModel cadModel =
                simulation.get(SolidModelManager.class).createSolidModel();

        cadModel.resetSystemOptions();

        ImportCadFileFeature importCadFileFeature =
                cadModel.importCadFile(resolvePath("C:\\Users\\NULS\\Desktop\\folder\\model.stp"), true, false, false, false, false, false, false, true, false, true, NeoProperty.fromString("{\'CGR\': 0, \'IFC\': 0, \'STEP\': 0, \'NX\': 0, \'CATIAV5\': 0, \'SE\': 0, \'JT\': 0}"), false);
//        ImportCadFileFeature importCadFileFeature =
//            cadModel.importCadFile(resolvePath(resultPath), true, false, false, false, false, false, false, true, false, true, NeoProperty.fromString("{\'CGR\': 0, \'IFC\': 0, \'STEP\': 0, \'NX\': 0, \'CATIAV5\': 0, \'SE\': 0, \'JT\': 0}"), false);

        star.cadmodeler.Body cadmodelerBody_0 =
                ((star.cadmodeler.Body) importCadFileFeature.getBodyByIndex(1));

        Face face_0 =
                ((Face) importCadFileFeature.getFaceByLocation(cadmodelerBody_0, new DoubleVector(new double[]{0.01, -0.22786658652431685, 0.10277042815333508})));

        cadModel.setFaceNameAttributes(new NeoObjectVector(new Object[]{face_0}), "Air blades", false);

        Face face_1 =
                ((Face) importCadFileFeature.getFaceByLocation(cadmodelerBody_0, new DoubleVector(new double[]{0.01, -0.23814856654623856, 0.4957704212230306})));

        cadModel.setFaceNameAttributes(new NeoObjectVector(new Object[]{face_1}), "Air input", false);

        Face face_2 =
                ((Face) importCadFileFeature.getFaceByLocation(cadmodelerBody_0, new DoubleVector(new double[]{0.01, -0.04266834375631555, -0.13363422057600013})));

        cadModel.setFaceNameAttributes(new NeoObjectVector(new Object[]{face_2}), "CH4", false);

        Face face_3 =
                ((Face) importCadFileFeature.getFaceByLocation(cadmodelerBody_0, new DoubleVector(new double[]{0.01, -0.042284638094857586, 0.08802686971235965})));

        cadModel.setFaceNameAttributes(new NeoObjectVector(new Object[]{face_3}), "No Gas 1", false);

        Face face_4 =
                ((Face) importCadFileFeature.getFaceByLocation(cadmodelerBody_0, new DoubleVector(new double[]{0.01, -0.009175941885004056, 0.004994952138007335})));

        cadModel.setFaceNameAttributes(new NeoObjectVector(new Object[]{face_4}), "No Gas 2", false);

        Face face_5 =
                ((Face) importCadFileFeature.getFaceByLocation(cadmodelerBody_0, new DoubleVector(new double[]{-10.667, 0.6180132, -0.6180132})));

        cadModel.setFaceNameAttributes(new NeoObjectVector(new Object[]{face_5}), "Outlet", false);

        cadmodelerBody_0.getUnNamedFacesDefaultAttributeName();

        // NamedFaces namedFaces_0 =
        //         ((NamedFaces) cadmodelerBody_0.getNamedFacesManager().getObject("ColoredFace1"));

        // cadmodelerBody_0.getNamedFacesManager().delete(new NeoObjectVector(new Object[]{namedFaces_0}));

        simulation.get(SolidModelManager.class).endEditCadModel(cadModel);

        cadModel.createParts(new NeoObjectVector(new Object[]{cadmodelerBody_0}), new NeoObjectVector(new Object[]{}), true, false, 1, false, false, 3, "SharpEdges", 30.0, 2, true, 1.0E-5, false);

    }

    private void createCylinderParts() {

        Simulation simulation =
                getActiveSimulation();

        Units units_0 =
                simulation.getUnitsManager().getPreferredUnits(Dimensions.Builder().length(1).build());

        MeshPartFactory meshPartFactory_0 =
                simulation.get(MeshPartFactory.class);

        SimpleCylinderPart simpleCylinderPart_0 =
                meshPartFactory_0.createNewCylinderPart(simulation.get(SimulationPartManager.class));

        simpleCylinderPart_0.setDoNotRetessellate(true);

        LabCoordinateSystem labCoordinateSystem_0 =
                simulation.getCoordinateSystemManager().getLabCoordinateSystem();

        simpleCylinderPart_0.setCoordinateSystem(labCoordinateSystem_0);

        simpleCylinderPart_0.getStartCoordinate().setCoordinateSystem(labCoordinateSystem_0);

        simpleCylinderPart_0.getStartCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[]{0.01, 0.0, 0.0}));

        simpleCylinderPart_0.getEndCoordinate().setCoordinateSystem(labCoordinateSystem_0);

        simpleCylinderPart_0.getEndCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[]{-0.667, 0.0, 0.0}));

        simpleCylinderPart_0.getRadius().setUnits(units_0);

        simpleCylinderPart_0.getRadius().setValue(0.721);

        simpleCylinderPart_0.getTessellationDensityOption().setSelected(TessellationDensityOption.Type.MEDIUM);

        simpleCylinderPart_0.rebuildSimpleShapePart();

        simpleCylinderPart_0.setDoNotRetessellate(false);

        SimpleCylinderPart simpleCylinderPart_1 =
                meshPartFactory_0.createNewCylinderPart(simulation.get(SimulationPartManager.class));

        simpleCylinderPart_1.setDoNotRetessellate(true);

        simpleCylinderPart_1.setCoordinateSystem(labCoordinateSystem_0);

        simpleCylinderPart_1.getStartCoordinate().setCoordinateSystem(labCoordinateSystem_0);

        simpleCylinderPart_1.getStartCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[]{-0.667, 0.0, 0.0}));

        simpleCylinderPart_1.getEndCoordinate().setCoordinateSystem(labCoordinateSystem_0);

        simpleCylinderPart_1.getEndCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[]{-1.111, 0.0, 0.0}));

        simpleCylinderPart_1.getRadius().setUnits(units_0);

        simpleCylinderPart_1.getRadius().setValue(0.535);

        simpleCylinderPart_1.getTessellationDensityOption().setSelected(TessellationDensityOption.Type.MEDIUM);

        simpleCylinderPart_1.rebuildSimpleShapePart();

        simpleCylinderPart_1.setDoNotRetessellate(false);

        SimpleCylinderPart simpleCylinderPart_2 =
                meshPartFactory_0.createNewCylinderPart(simulation.get(SimulationPartManager.class));

        simpleCylinderPart_2.setDoNotRetessellate(true);

        simpleCylinderPart_2.setCoordinateSystem(labCoordinateSystem_0);

        simpleCylinderPart_2.getStartCoordinate().setCoordinateSystem(labCoordinateSystem_0);

        simpleCylinderPart_2.getStartCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[]{-1.111, 0.0, 0.0}));

        simpleCylinderPart_2.getEndCoordinate().setCoordinateSystem(labCoordinateSystem_0);

        simpleCylinderPart_2.getEndCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[]{-10.667, 0.0, 0.0}));

        simpleCylinderPart_2.getRadius().setUnits(units_0);

        simpleCylinderPart_2.getRadius().setValue(1.5);

        simpleCylinderPart_2.getTessellationDensityOption().setSelected(TessellationDensityOption.Type.MEDIUM);

        simpleCylinderPart_2.rebuildSimpleShapePart();

        simpleCylinderPart_2.setDoNotRetessellate(false);

    }

    private void createVolumeMeshControl(){

        Simulation simulation =
                getActiveSimulation();

        Units units_0 =
                simulation.getUnitsManager().getPreferredUnits(Dimensions.Builder().length(1).build());

        SimpleCylinderPart simpleCylinderPart_0 =
                ((SimpleCylinderPart) simulation.get(SimulationPartManager.class).getPart("Cylinder"));

        SimpleCylinderPart simpleCylinderPart_1 =
                ((SimpleCylinderPart) simulation.get(SimulationPartManager.class).getPart("Cylinder 2"));

        SimpleCylinderPart simpleCylinderPart_2 =
                ((SimpleCylinderPart) simulation.get(SimulationPartManager.class).getPart("Cylinder 3"));

        SolidModelPart solidModelPart_0 =
                ((SolidModelPart) simulation.get(SimulationPartManager.class).getPart("Body 1"));

        AutoMeshOperation autoMeshOperation_0 =
                simulation.get(MeshOperationManager.class).createAutoMeshOperation(new StringVector(new String[]{"star.resurfacer.ResurfacerAutoMesher", "star.dualmesher.DualAutoMesher"}), new NeoObjectVector(new Object[]{solidModelPart_0}));

        VolumeCustomMeshControl volumeCustomMeshControl_0 =
                autoMeshOperation_0.getCustomMeshControls().createVolumeControl();

        VolumeCustomMeshControl volumeCustomMeshControl_1 =
                autoMeshOperation_0.getCustomMeshControls().createVolumeControl();

        VolumeCustomMeshControl volumeCustomMeshControl_2 =
                autoMeshOperation_0.getCustomMeshControls().createVolumeControl();

        SurfaceCustomMeshControl surfaceCustomMeshControl_0 =
                autoMeshOperation_0.getCustomMeshControls().createSurfaceControl();

        volumeCustomMeshControl_0.getGeometryObjects().setQuery(null);

        volumeCustomMeshControl_0.getGeometryObjects().setObjects(simpleCylinderPart_0);

        volumeCustomMeshControl_1.getGeometryObjects().setQuery(null);

        volumeCustomMeshControl_1.getGeometryObjects().setObjects(simpleCylinderPart_1);

        volumeCustomMeshControl_2.getGeometryObjects().setQuery(null);

        volumeCustomMeshControl_2.getGeometryObjects().setObjects(simpleCylinderPart_2);

        surfaceCustomMeshControl_0.getGeometryObjects().setQuery(null);

        PartSurface partSurface_0 =
                ((PartSurface) solidModelPart_0.getPartSurfaceManager().getPartSurface("Default"));

        PartSurface partSurface_1 =
                ((PartSurface) solidModelPart_0.getPartSurfaceManager().getPartSurface("Outlet"));

        surfaceCustomMeshControl_0.getGeometryObjects().setObjects(partSurface_0, partSurface_1);

        surfaceCustomMeshControl_0.getCustomConditions().get(PartsTargetSurfaceSizeOption.class).setSelected(PartsTargetSurfaceSizeOption.Type.CUSTOM);

        PartsTargetSurfaceSize partsTargetSurfaceSize_0 =
                surfaceCustomMeshControl_0.getCustomValues().get(PartsTargetSurfaceSize.class);

        Units units_1 =
                ((Units) simulation.getUnitsManager().getObject(""));

        partsTargetSurfaceSize_0.getRelativeSizeScalar().setValueAndUnits(20.0, units_1);

        VolumeControlResurfacerSizeOption volumeControlResurfacerSizeOption_0 =
                volumeCustomMeshControl_0.getCustomConditions().get(VolumeControlResurfacerSizeOption.class);

        volumeControlResurfacerSizeOption_0.setVolumeControlBaseSizeOption(true);

        VolumeControlDualMesherSizeOption volumeControlDualMesherSizeOption_0 =
                volumeCustomMeshControl_0.getCustomConditions().get(VolumeControlDualMesherSizeOption.class);

        volumeControlDualMesherSizeOption_0.setVolumeControlBaseSizeOption(true);

        VolumeControlSize volumeControlSize_0 =
                volumeCustomMeshControl_0.getCustomValues().get(VolumeControlSize.class);

        volumeControlSize_0.getRelativeSizeScalar().setValueAndUnits(2.0, units_1);

        VolumeControlResurfacerSizeOption volumeControlResurfacerSizeOption_1 =
                volumeCustomMeshControl_1.getCustomConditions().get(VolumeControlResurfacerSizeOption.class);

        volumeControlResurfacerSizeOption_1.setVolumeControlBaseSizeOption(true);

        VolumeControlDualMesherSizeOption volumeControlDualMesherSizeOption_1 =
                volumeCustomMeshControl_1.getCustomConditions().get(VolumeControlDualMesherSizeOption.class);

        volumeControlDualMesherSizeOption_1.setVolumeControlBaseSizeOption(true);

        VolumeControlSize volumeControlSize_1 =
                volumeCustomMeshControl_1.getCustomValues().get(VolumeControlSize.class);

        volumeControlSize_1.getRelativeSizeScalar().setValueAndUnits(2.0, units_1);

        VolumeControlResurfacerSizeOption volumeControlResurfacerSizeOption_2 =
                volumeCustomMeshControl_2.getCustomConditions().get(VolumeControlResurfacerSizeOption.class);

        volumeControlResurfacerSizeOption_2.setVolumeControlBaseSizeOption(true);

        VolumeControlDualMesherSizeOption volumeControlDualMesherSizeOption_2 =
                volumeCustomMeshControl_2.getCustomConditions().get(VolumeControlDualMesherSizeOption.class);

        volumeControlDualMesherSizeOption_2.setVolumeControlBaseSizeOption(true);

        VolumeControlSize volumeControlSize_2 =
                volumeCustomMeshControl_2.getCustomValues().get(VolumeControlSize.class);

        volumeControlSize_2.getRelativeSizeScalar().setValueAndUnits(5.0, units_1);

        autoMeshOperation_0.getDefaultValues().get(BaseSize.class).setValueAndUnits(0.5, units_0);

    }

    private void createBoundaries() {

        Simulation simulation =
                getActiveSimulation();

        SolidModelPart solidModelPart =
                ((SolidModelPart) simulation.get(SimulationPartManager.class).getPart("Body 1"));

        simulation.getRegionManager().newRegionsFromParts(new NeoObjectVector(new Object[]{solidModelPart}), "OneRegionPerPart", null, "OneBoundaryPerPartSurface", null, "OneFeatureCurve", null, RegionManager.CreateInterfaceMode.BOUNDARY, "OneEdgeBoundaryPerPart", null);

        Region region_0 =
                simulation.getRegionManager().getRegion("Body 1");

        Boundary boundary_0 =
                region_0.getBoundaryManager().getBoundary("Air blades");

        InletBoundary inletBoundary_0 =
                ((InletBoundary) simulation.get(ConditionTypeManager.class).get(InletBoundary.class));

        boundary_0.setBoundaryType(inletBoundary_0);

        Boundary boundary_1 =
                region_0.getBoundaryManager().getBoundary("Air input");

        boundary_1.setBoundaryType(inletBoundary_0);

        Boundary boundary_2 =
                region_0.getBoundaryManager().getBoundary("CH4");

        MassFlowBoundary massFlowBoundary_0 =
                ((MassFlowBoundary) simulation.get(ConditionTypeManager.class).get(MassFlowBoundary.class));

        boundary_2.setBoundaryType(massFlowBoundary_0);

        Boundary boundary_3 =
                region_0.getBoundaryManager().getBoundary("Outlet");

        PressureBoundary pressureBoundary_0 =
                ((PressureBoundary) simulation.get(ConditionTypeManager.class).get(PressureBoundary.class));

        boundary_3.setBoundaryType(pressureBoundary_0);

    }

    private void generateVolumeMesh() {

        Simulation simulation =
                getActiveSimulation();

        MeshPipelineController meshPipelineController =
                simulation.get(MeshPipelineController.class);

        meshPipelineController.generateVolumeMesh();

    }

    private void createPlaneSection() {

        Simulation simulation =
                getActiveSimulation();

        Units units_0 =
                simulation.getUnitsManager().getPreferredUnits(Dimensions.Builder().length(1).build());

        Region region =
                simulation.getRegionManager().getRegion("Body 1");

        PlaneSection planeSection =
                (PlaneSection) simulation.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));

        LabCoordinateSystem labCoordinateSystem =
                simulation.getCoordinateSystemManager().getLabCoordinateSystem();

        planeSection.setCoordinateSystem(labCoordinateSystem);

        planeSection.getInputParts().setQuery(null);

        planeSection.getInputParts().setObjects(region);

        planeSection.getOriginCoordinate().setUnits0(units_0);

        planeSection.getOriginCoordinate().setUnits1(units_0);

        planeSection.getOriginCoordinate().setUnits2(units_0);

        planeSection.getOriginCoordinate().setDefinition("");

        planeSection.getOriginCoordinate().setValue(new DoubleVector(new double[] {-5.32849999999975, 0.0, 0.0}));

        planeSection.getOriginCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {-5.32849999999975, 0.0, 0.0}));

        planeSection.getOriginCoordinate().setCoordinateSystem(labCoordinateSystem);

        planeSection.getOrientationCoordinate().setUnits0(units_0);

        planeSection.getOrientationCoordinate().setUnits1(units_0);

        planeSection.getOrientationCoordinate().setUnits2(units_0);

        planeSection.getOrientationCoordinate().setDefinition("");

        planeSection.getOrientationCoordinate().setValue(new DoubleVector(new double[] {0.0, 0.0, 1.0}));

        planeSection.getOrientationCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 0.0, 1.0}));

        planeSection.getOrientationCoordinate().setCoordinateSystem(labCoordinateSystem);

        SingleValue singleValue =
                planeSection.getSingleValue();

        singleValue.getValueQuantity().setValue(0.0);

        singleValue.getValueQuantity().setUnits(units_0);

        RangeMultiValue rangeMultiValue =
                planeSection.getRangeMultiValue();

        rangeMultiValue.setNValues(2);

        rangeMultiValue.getStartQuantity().setValue(0.0);

        rangeMultiValue.getStartQuantity().setUnits(units_0);

        rangeMultiValue.getEndQuantity().setValue(1.0);

        rangeMultiValue.getEndQuantity().setUnits(units_0);

        DeltaMultiValue deltaMultiValue =
                planeSection.getDeltaMultiValue();

        deltaMultiValue.setNValues(2);

        deltaMultiValue.getStartQuantity().setValue(0.0);

        deltaMultiValue.getStartQuantity().setUnits(units_0);

        deltaMultiValue.getDeltaQuantity().setValue(1.0);

        deltaMultiValue.getDeltaQuantity().setUnits(units_0);

        MultiValue multiValue =
                planeSection.getArbitraryMultiValue();

        multiValue.getValueQuantities().setUnits(units_0);

        multiValue.getValueQuantities().setArray(new DoubleVector(new double[] {0.0}));

        planeSection.setValueMode(ValueMode.SINGLE);

        PlaneSection planeSection_2 =
                (PlaneSection) simulation.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 1.0}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), 0, 1, new DoubleVector(new double[] {0.0}));

        planeSection_2.setCoordinateSystem(labCoordinateSystem);

        planeSection_2.getInputParts().setQuery(null);

        planeSection_2.getInputParts().setObjects(region);

        planeSection_2.getOriginCoordinate().setUnits0(units_0);

    }

    private void settingPhysicsContinuum() {

        Simulation simulation =
                getActiveSimulation();

        PhysicsContinuum physicsContinuum =
                ((PhysicsContinuum) simulation.getContinuumManager().getContinuum("Physics 1"));

        physicsContinuum.enable(MultiComponentGasModel.class);

        physicsContinuum.enable(ReactingModel.class);

        physicsContinuum.enable(FlameletBasedModel.class);

        physicsContinuum.enable(FgmCombustionModel.class);

        physicsContinuum.enable(FgmReactionModel.class);

        physicsContinuum.enable(FgmIdealGasModel.class);

        physicsContinuum.enable(TfcCombustionPartiallyPremixedModel.class);

        physicsContinuum.enable(SegregatedFlowModel.class);

        physicsContinuum.enable(TurbulentModel.class);

        physicsContinuum.enable(SegregatedFluidEnthalpyModel.class);

        physicsContinuum.enable(RansTurbulenceModel.class);

        physicsContinuum.enable(KEpsilonTurbulence.class);

        physicsContinuum.enable(RkeTwoLayerTurbModel.class);

        physicsContinuum.enable(KeTwoLayerAllYplusWallTreatment.class);

        physicsContinuum.enable(SteadyModel.class);

        physicsContinuum.enable(AmrModel.class);

        physicsContinuum.enable(NoxModel.class);

        physicsContinuum.enable(ThermalNoxModel.class);

        physicsContinuum.enable(NoxThreeEquationZeldovichModel.class);

        ProgressVariableIgnitor progressVariableIgnitor =
                physicsContinuum.get(IgnitorManager.class).createIgnitor(ProgressVariableIgnitor.class);

        progressVariableIgnitor.getGeometryPartGroup().setQuery(null);

        SimpleCylinderPart simpleCylinderPart =
                ((SimpleCylinderPart) simulation.get(SimulationPartManager.class).getPart("Cylinder 3"));

        progressVariableIgnitor.getGeometryPartGroup().setObjects(simpleCylinderPart);

        PulseActivator pulseActivator =
                ((PulseActivator) progressVariableIgnitor.getActivator());

        Units units_1 =
                ((Units) simulation.getUnitsManager().getObject(""));

        pulseActivator.getBegin().setValueAndUnits(200.0, units_1);

        pulseActivator.getEnd().setValueAndUnits(300.0, units_1);
    }

    private void createFgmTable() {
        Simulation simulation =
                getActiveSimulation();

        PhysicsContinuum physicsContinuum =
                ((PhysicsContinuum) simulation.getContinuumManager().getContinuum("Physics 1"));

        Region region_0 =
                simulation.getRegionManager().getRegion("Body 1");

        FgmTableGenerator fgmTableGenerator =
                physicsContinuum.get(FgmTableGenerator.class);

        FgmTableParameters fgmTableParameters =
                ((FgmTableParameters) fgmTableGenerator.getTableParameters());

        fgmTableParameters.getTableProgressVariableDefinition().setSelected(TableProgressVariableDefinitionOption.Type.CHEMENTHALPY);

        Fgm0dIgnitionNumericalSettings fgm0dIgnitionNumericalSettings =
                ((Fgm0dIgnitionNumericalSettings) fgmTableParameters.getFgm0dIgnitionNumericalSettings());

        TableAxisParameters tableAxisParameters_0 =
                ((TableAxisParameters) fgm0dIgnitionNumericalSettings.getTableAxisParametersManager().getComponent("Heat Loss Ratio"));

        tableAxisParameters_0.setAdapt(false);

        FixedGridParameters fixedGridParameters_0 =
                tableAxisParameters_0.getFixedGridParameters();

        IntegerValue integerValue_0 =
                fixedGridParameters_0.getDimensionSizeValue();

        integerValue_0.getQuantity().setValue(1.0);

        TableChemistryDefinition tableChemistryDefinition_0 =
                ((TableChemistryDefinition) fgmTableGenerator.getTableChemistryDefinition());

        tableChemistryDefinition_0.importCaseFromChemkin(resolvePath(GRIMECH30_PATH), resolvePath(THERMO30_PATH), resolvePath(TRANSPORT_PATH), "", "");

        TableFluidStreamCollection tableFluidStreamCollection_0 =
                ((TableFluidStreamCollection) fgmTableGenerator.getTableFluidStreamCollection());

        TableFluidStream tableFluidStream_0 =
                ((TableFluidStream) tableFluidStreamCollection_0.getOxidizerStream());

        Units units_2 =
                ((Units) simulation.getUnitsManager().getObject("K"));

        tableFluidStream_0.getTemperature().setValueAndUnits(477.0, units_2);

        TableFluidStream tableFluidStream_1 =
                ((TableFluidStream) tableFluidStreamCollection_0.getFuelStream());

        tableFluidStream_1.getFluidStreamComposition().setArray(new DoubleVector(new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}));

        TableSpeciesForPostProcessing tableSpeciesForPostProcessing_0 =
                ((TableSpeciesForPostProcessing) fgmTableParameters.getTableSpeciesForPostProcessing());

        ((TableSpeciesGroup) tableSpeciesForPostProcessing_0.getTableSpeciesGroup()).setQuery(null);

        star.material.MaterialDataBase materialMaterialDataBase_0 =
                simulation.get(MaterialDataBaseManager.class).getMatlDataBase("Table Generator");

        star.material.DataBaseMaterialManager materialDataBaseMaterialManager_0 =
                materialMaterialDataBase_0.getFolder("Physics 1-Fgm");

        star.material.DataBaseGas materialDataBaseGas_0 =
                ((star.material.DataBaseGas) materialDataBaseMaterialManager_0.getMaterial("CH4_Gas"));

        star.material.DataBaseGas materialDataBaseGas_1 =
                ((star.material.DataBaseGas) materialDataBaseMaterialManager_0.getMaterial("CO_Gas"));

        star.material.DataBaseGas materialDataBaseGas_2 =
                ((star.material.DataBaseGas) materialDataBaseMaterialManager_0.getMaterial("CO2_Gas"));

        star.material.DataBaseGas materialDataBaseGas_3 =
                ((star.material.DataBaseGas) materialDataBaseMaterialManager_0.getMaterial("H2O_Gas"));

        star.material.DataBaseGas materialDataBaseGas_4 =
                ((star.material.DataBaseGas) materialDataBaseMaterialManager_0.getMaterial("N2_Gas"));

        star.material.DataBaseGas materialDataBaseGas_5 =
                ((star.material.DataBaseGas) materialDataBaseMaterialManager_0.getMaterial("NO_Gas"));

        star.material.DataBaseGas materialDataBaseGas_6 =
                ((star.material.DataBaseGas) materialDataBaseMaterialManager_0.getMaterial("NO2_Gas"));

        star.material.DataBaseGas materialDataBaseGas_7 =
                ((star.material.DataBaseGas) materialDataBaseMaterialManager_0.getMaterial("O2_Gas"));

        star.material.DataBaseGas materialDataBaseGas_8 =
                ((star.material.DataBaseGas) materialDataBaseMaterialManager_0.getMaterial("OH_Gas"));

        ((TableSpeciesGroup) tableSpeciesForPostProcessing_0.getTableSpeciesGroup()).setObjects(materialDataBaseGas_0, materialDataBaseGas_1, materialDataBaseGas_2, materialDataBaseGas_3, materialDataBaseGas_4, materialDataBaseGas_5, materialDataBaseGas_6, materialDataBaseGas_7, materialDataBaseGas_8);

        FgmTable fgmTable_0 =
                ((FgmTable) fgmTableGenerator.getFgmTable());

        fgmTable_0.constructTable();

        Boundary boundary_0 =
                region_0.getBoundaryManager().getBoundary("Air blades");

        VelocityMagnitudeProfile velocityMagnitudeProfile_0 =
                boundary_0.getValues().get(VelocityMagnitudeProfile.class);

        Units units_3 =
                ((Units) simulation.getUnitsManager().getObject("m/s"));

        velocityMagnitudeProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValueAndUnits(77.01, units_3);

        Boundary boundary_1 =
                region_0.getBoundaryManager().getBoundary("Air input");

        VelocityMagnitudeProfile velocityMagnitudeProfile_1 =
                boundary_1.getValues().get(VelocityMagnitudeProfile.class);

        velocityMagnitudeProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValueAndUnits(30.23, units_3);

        Boundary boundary_2 =
                region_0.getBoundaryManager().getBoundary("CH4");

        MixtureFractionArrayProfile mixtureFractionArrayProfile_0 =
                boundary_2.getValues().get(MixtureFractionArrayProfile.class);

        mixtureFractionArrayProfile_0.getMethod(ConstantArrayProfileMethod.class).getQuantity().setArray(new DoubleVector(new double[] {1.0}));

        MassFlowRateProfile massFlowRateProfile_0 =
                boundary_2.getValues().get(MassFlowRateProfile.class);

        Units units_4 =
                ((Units) simulation.getUnitsManager().getObject("kg/s"));

        massFlowRateProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValueAndUnits(0.7334, units_4);
    }

    private void createLinePart() {

        Simulation simulation =
                getActiveSimulation();

        LabCoordinateSystem labCoordinateSystem =
                simulation.getCoordinateSystemManager().getLabCoordinateSystem();

        Units units_0 =
                simulation.getUnitsManager().getPreferredUnits(Dimensions.Builder().length(1).build());

        Region region_0 =
                simulation.getRegionManager().getRegion("Body 1");

        LinePart linePart_0 =
                simulation.getPartManager().createLinePart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), new DoubleVector(new double[] {1.0, 0.0, 0.0}), 20);

        linePart_0.getPoint1Coordinate().setCoordinateSystem(labCoordinateSystem);

        linePart_0.getPoint1Coordinate().setUnits0(units_0);

        linePart_0.getPoint1Coordinate().setUnits1(units_0);

        linePart_0.getPoint1Coordinate().setUnits2(units_0);

        linePart_0.getPoint1Coordinate().setDefinition("");

        linePart_0.getPoint1Coordinate().setValue(new DoubleVector(new double[] {-10.666999816894531, 0.0, 0.0}));

        linePart_0.getPoint1Coordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {-10.666999816894531, 0.0, 0.0}));

        linePart_0.getPoint2Coordinate().setCoordinateSystem(labCoordinateSystem);

        linePart_0.getPoint2Coordinate().setUnits0(units_0);

        linePart_0.getPoint2Coordinate().setUnits1(units_0);

        linePart_0.getPoint2Coordinate().setUnits2(units_0);

        linePart_0.getPoint2Coordinate().setDefinition("");

        linePart_0.getPoint2Coordinate().setValue(new DoubleVector(new double[] {0.009999999776482582, 0.0, 0.0}));

        linePart_0.getPoint2Coordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.009999999776482582, 0.0, 0.0}));

        linePart_0.setCoordinateSystem(labCoordinateSystem);

        linePart_0.getInputParts().setQuery(null);

        linePart_0.getInputParts().setObjects(region_0);

        linePart_0.setResolution(150);

        LinePart linePart_1 =
                simulation.getPartManager().createLinePart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {0.0, 0.0, 0.0}), new DoubleVector(new double[] {1.0, 0.0, 0.0}), 20);

        linePart_1.getPoint1Coordinate().setCoordinateSystem(labCoordinateSystem);

        linePart_1.getPoint1Coordinate().setUnits0(units_0);

        linePart_1.getPoint1Coordinate().setUnits1(units_0);

        linePart_1.getPoint1Coordinate().setUnits2(units_0);

        linePart_1.getPoint1Coordinate().setDefinition("");

        linePart_1.getPoint1Coordinate().setValue(new DoubleVector(new double[] {-3.0, 2.0, 0.0}));

        linePart_1.getPoint1Coordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {-3.0, 2.0, 0.0}));

        linePart_1.getPoint2Coordinate().setCoordinateSystem(labCoordinateSystem);

        linePart_1.getPoint2Coordinate().setUnits0(units_0);

        linePart_1.getPoint2Coordinate().setUnits1(units_0);

        linePart_1.getPoint2Coordinate().setUnits2(units_0);

        linePart_1.getPoint2Coordinate().setDefinition("");

        linePart_1.getPoint2Coordinate().setValue(new DoubleVector(new double[] {-3.0, -2.0, 0.0}));

        linePart_1.getPoint2Coordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {-3.0, -2.0, 0.0}));

        linePart_1.setCoordinateSystem(labCoordinateSystem);

        linePart_1.getInputParts().setQuery(null);

        linePart_1.getInputParts().setObjects(region_0);

        linePart_1.setResolution(50);
    }

    private void settingPlaneSection() {

        Simulation simulation =
                getActiveSimulation();

        PlaneSection planeSection_1 =
                ((PlaneSection) simulation.getPartManager().getObject("Plane Section 2"));

        Units units_0 =
                ((Units) simulation.getUnitsManager().getObject("m"));

        planeSection_1.getOriginCoordinate().setUnits1(units_0);

        planeSection_1.getOriginCoordinate().setUnits2(units_0);

        planeSection_1.getOriginCoordinate().setDefinition("");

        planeSection_1.getOriginCoordinate().setValue(new DoubleVector(new double[] {-1.6709601589386591, 0.0, 0.0}));

        planeSection_1.getOriginCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {-1.6709601589386591, 0.0, 0.0}));

        LabCoordinateSystem labCoordinateSystem_0 =
                simulation.getCoordinateSystemManager().getLabCoordinateSystem();

        planeSection_1.getOriginCoordinate().setCoordinateSystem(labCoordinateSystem_0);

        planeSection_1.getOrientationCoordinate().setUnits0(units_0);

        planeSection_1.getOrientationCoordinate().setUnits1(units_0);

        planeSection_1.getOrientationCoordinate().setUnits2(units_0);

        planeSection_1.getOrientationCoordinate().setDefinition("");

        planeSection_1.getOrientationCoordinate().setValue(new DoubleVector(new double[] {1.0, 0.0, 0.0}));

        planeSection_1.getOrientationCoordinate().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {1.0, 0.0, 0.0}));

        planeSection_1.getOrientationCoordinate().setCoordinateSystem(labCoordinateSystem_0);

        SingleValue singleValue_1 =
                planeSection_1.getSingleValue();

        singleValue_1.getValueQuantity().setValue(0.0);

        singleValue_1.getValueQuantity().setUnits(units_0);

        RangeMultiValue rangeMultiValue_1 =
                planeSection_1.getRangeMultiValue();

        rangeMultiValue_1.setNValues(2);

        rangeMultiValue_1.getStartQuantity().setValue(0.0);

        rangeMultiValue_1.getStartQuantity().setUnits(units_0);

        rangeMultiValue_1.getEndQuantity().setValue(1.0);

        rangeMultiValue_1.getEndQuantity().setUnits(units_0);

        DeltaMultiValue deltaMultiValue_1 =
                planeSection_1.getDeltaMultiValue();

        deltaMultiValue_1.setNValues(2);

        deltaMultiValue_1.getStartQuantity().setValue(0.0);

        deltaMultiValue_1.getStartQuantity().setUnits(units_0);

        deltaMultiValue_1.getDeltaQuantity().setValue(1.0);

        deltaMultiValue_1.getDeltaQuantity().setUnits(units_0);

        MultiValue multiValue_1 =
                planeSection_1.getArbitraryMultiValue();

        multiValue_1.getValueQuantities().setUnits(units_0);

        multiValue_1.getValueQuantities().setArray(new DoubleVector(new double[] {0.0}));

        planeSection_1.setValueMode(ValueMode.SINGLE);
    }

    private void createPlot() {

        Simulation simulation =
                getActiveSimulation();

        Units units_0 =
                ((Units) simulation.getUnitsManager().getObject("m"));

        XYPlot xYPlot =
                simulation.getPlotManager().createPlot(XYPlot.class);

        PlotUpdate plotUpdate =
                xYPlot.getPlotUpdate();

        HardcopyProperties hardcopyProperties =
                plotUpdate.getHardcopyProperties();

        hardcopyProperties.setCurrentResolutionWidth(25);

        hardcopyProperties.setCurrentResolutionHeight(25);

        xYPlot.getParts().setQuery(null);

        LinePart linePart_0 =
                ((LinePart) simulation.getPartManager().getObject("Line Probe"));

        xYPlot.getParts().setObjects(linePart_0);

        AxisType axisType =
                xYPlot.getXAxisType();

        axisType.getDirectionVector().setComponentsAndUnits(-1.0, 0.0, 0.0, units_0);

        YAxisType yAxisType =
                ((YAxisType) xYPlot.getYAxes().getAxisType("Y Type 1"));

        FieldFunctionUnits fieldFunctionUnits =
                yAxisType.getScalarFunction();

        PrimitiveFieldFunction primitiveFieldFunction =
                ((PrimitiveFieldFunction) simulation.getFieldFunctionManager().getFunction("Temperature"));

        fieldFunctionUnits.setFieldFunction(primitiveFieldFunction);
    }

    private void setStoppingCriterion(int maxStep) {

        Simulation simulation =
                getActiveSimulation();

        StepStoppingCriterion stepStoppingCriterion = ((StepStoppingCriterion) simulation.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));
        stepStoppingCriterion.setMaximumNumberSteps(maxStep);

    }

    private void runSimulation() {

        Simulation simulation =
                getActiveSimulation();

        ResidualPlot residualPlot_0 =
                ((ResidualPlot) simulation.getPlotManager().getPlot("Residuals"));

        residualPlot_0.open();

        simulation.getSimulationIterator().run();

    }
// сколько строк столько и файлов
    private void saveState() {

        Simulation simulation =
                getActiveSimulation();

        simulation.saveState("D:\\Projects\\Physics\\Labs\\Materials to work\\burner\\b_5_5.sim");

    }

    private int repeatGeometryCheck(int rowNumber,String exclelPath) throws IOException, InterruptedException{
        
            FileInputStream file = new FileInputStream(new File(exclelPath));
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);
        
            int rowStart  = EXCEL_START_ROW;
            int collStart = EXCEL_START_COLL;

            List<Integer> numbersStart = new ArrayList<Integer>();

            for(int i = collStart; i < collStart + 7; i++){
                numbersStart.add((int)sheet.getRow(rowNumber).getCell(i).getNumericCellValue());
            }

            for(int i = rowNumber-1; i > rowStart;i--){

                List<Integer> numbersCheck = new ArrayList<Integer>();

                for(int j = collStart; j < collStart + 7; j++){
                    numbersCheck.add((int)sheet.getRow(i).getCell(j).getNumericCellValue());
                }

                if (numbersStart.equals(numbersCheck)) {
                    return i;
                }
            }

            return 0;
    }

    private int getColumnsGeometryNumber(String exclelPath)
            throws IOException, InterruptedException {
        try {
            FileInputStream file = new FileInputStream(new File(exclelPath));
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);
        
            int rowStart  = EXCEL_START_ROW;
            int collStart = EXCEL_START_COLL;
            int coll_number = 0;

            while(true) {
                try{
                    double value = sheet.getRow(rowStart).getCell(collStart).getNumericCellValue();
                    rowStart ++;
                    coll_number++;
                }
                catch(Exception exp){
                    return coll_number;
                }

            }
        }
        catch(Exception exp){
            return 0;
        }
    }

    private void changeGeometry(String geometryPath, String excelPath,int rowNumber,int collNumber,String pyScriptPath) throws InterruptedException, IOException {

        //Runtime.getRuntime().exec("cmd /c start cmd.exe /K python C:\\Users\\NULS\\PycharmProjects\\compasWork\\geom.py"+" "+geometryPath+" " + excelPath+" " + Integer.toString(rowNumber));

        ProcessBuilder builder = new ProcessBuilder("python",
                pyScriptPath,
                geometryPath,
                excelPath,
                Integer.toString(rowNumber),
                Integer.toString(collNumber));
        Process process = builder.start();
        process.waitFor();
    }

    private void getConfigData(String configPath){
        Properties props = new Properties();

        try {
                props.load(new FileInputStream(new File(configPath)));
        } catch (IOException ex) {
                throw new RuntimeException(ex);
        }

        PYTHON_SCRIPT_PATH = props.getProperty("PYTHON_SCRIPT_PATH");
        GRIMECH30_PATH = props.getProperty("GRIMECH30_PATH");
        THERMO30_PATH = props.getProperty("THERMO30_PATH");
        TRANSPORT_PATH = props.getProperty("TRANSPORT_PATH");
        EXCEL_START_ROW = Integer.valueOf(props.getProperty("EXCEL_START_ROW"));
        EXCEL_START_COLL = Integer.valueOf(props.getProperty("EXCEL_START_COLL"));
    }

//Вызываем если геометрия изменилась
    private void isGeometryChange(int i){
        try {
                //методS
                changeGeometry(lbGeometry.getText(),
                        lbExcel.getText(),
                        EXCEL_START_ROW+i,
                        EXCEL_START_COLL,
                        PYTHON_SCRIPT_PATH);
        } catch (InterruptedException | IOException ex) {
                throw new RuntimeException(ex);
        }
        //запустить новый процесс стар
    }

//Вызываем если геометрия не изменялась
    private void isGeometryDontChange(){

    }

    private void formChoose(){

        JFrame frame = new JFrame();

        //Geometry File
        JLabel lbGeometry = new JLabel("Geometry File");
        JButton btnGeometry = new JButton("Choose Geometry");
        btnGeometry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle("Select a geometry");
                int response = fileChooser.showOpenDialog(null);

                if(response == JFileChooser.APPROVE_OPTION){
                    File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    lbGeometry.setText(file.toString());
                }
            }
        });

        // Excel File
        JLabel lbExcel = new JLabel("ExcelFile");
        JButton btnExcel = new JButton("Choose Excel File");
        btnExcel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle("Select a Excel");
                int response = fileChooser.showOpenDialog(null);

                if(response == JFileChooser.APPROVE_OPTION){
                    File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    lbExcel.setText(file.toString());
                }
            }
        });

        // Save Directory
        JLabel lblSaveDirectory = new JLabel(" Save Directory Choose");
        JButton btnSaveDirectory = new JButton("Save Directory");
        btnSaveDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser directoryChooser = new JFileChooser();
                directoryChooser.setCurrentDirectory(new java.io.File("."));
                directoryChooser.setDialogTitle("Save Directory Choose");
                directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                directoryChooser.setAcceptAllFileFilterUsed(false);
                int response = directoryChooser.showOpenDialog(null);

                if(response == JFileChooser.APPROVE_OPTION){
                    var directory = directoryChooser.getSelectedFile();
                    lblSaveDirectory.setText(directory.toString());
                }
            }
        });

        JPanel jpForm = new JPanel();
        jpForm.setLayout(new GridLayout(3,2));
        jpForm.add(lbGeometry);
        jpForm.add(btnGeometry);
        jpForm.add(lbExcel);
        jpForm.add(btnExcel);
        jpForm.add(lblSaveDirectory);
        jpForm.add(btnSaveDirectory);

        JButton btnOK = new JButton("Ok");
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                frame.dispose();
                //количество строк в ексель файле
                int columnNumber = 0;
                try {
                    //метод подсчета строк
                    columnNumber = getColumnsGeometryNumber(lbExcel.getText());

                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                JOptionPane.showMessageDialog(
                        null, Integer.toString(columnNumber)
                );

                // основной цикл работы
                for(int i = 0; i < columnNumber;i++){
                        int repeatRow = 0;
                        try {
                                // проверка на повторение геометрии
                                repeatRow = repeatGeometryCheck(EXCEL_START_ROW+i,lbExcel.getText());

                                JOptionPane.showMessageDialog(
                                        null, repeatRow+""
                                );
                        } 
                        catch (IOException | InterruptedException ex) {
                                throw new RuntimeException(ex);
                        }

                        if(repeatRow == 0){
                           isGeometryChange()
                        }
                        else{
                           isGeometryDontChange()
                        }
                }

                // методы работы в Star CCM+
                
        //        importGeometry();

        //        createCylinderParts();

        //        createVolumeMeshControl();

        //        createBoundaries();

        //        generateVolumeMesh();

        //        createPlaneSection();

        //        settingPhysicsContinuum();

        //        createFgmTable();

        //        createLinePart();
 
        //        settingPlaneSection();

        //        createPlot();

        //        setStoppingCriterion(7000);

                JOptionPane.showMessageDialog(
                        null, "Complete!"
                );
            }
        });

        JPanel jpOK = new JPanel();
        jpOK.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = GridBagConstraints.RELATIVE;
        jpOK.add(btnOK,constraints);

        JPanel jpMainPannel = new JPanel();
        jpMainPannel.setLayout(new BorderLayout(10,10));
        jpMainPannel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        jpMainPannel.add(jpForm,BorderLayout.NORTH);
        jpMainPannel.add(jpOK);

        frame.add(jpMainPannel);


        frame.setMinimumSize(new Dimension(500,200));
        frame.setVisible(true);
    }
}