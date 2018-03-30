import React from 'react';
import { connect } from 'react-redux';
import { Row, Col, Panel, Image, Label } from 'react-bootstrap';
import R from 'ramda';
import { kebabCase, ShipLink, ShipModificationLink, OutfitLink, renderAttribute, intersperse } from '../common';

const OutfitDescription = ({ description }) => {
  if (description.length === 0) {
    return <p className="italic">No description.</p>;
  } else {
    return intersperse(description, index => <span key={index}><br/><br/></span>);
  }
};

const AmmoItem = ({ object }) => {
  if (object.ammo) {
    return <li>ammo: <OutfitLink outfitName={object.ammo} /></li>;
  } else {
    return null;
  }
};

const WeaponAttributes = ({ weapon }) => {
  return (
    <div>
      <br />
      <AmmoItem object={weapon} />
      {renderAttribute(weapon, R.prop('range'),                           'range')}
      {renderAttribute(weapon, R.path(['shieldDamage', 'perSecond']),     'shield damage / second')}
      {renderAttribute(weapon, R.path(['hullDamage', 'perSecond']),       'hull damage / second')}
      {renderAttribute(weapon, R.path(['heatDamage', 'perSecond']),       'heat damage / second')}
      {renderAttribute(weapon, R.path(['ionDamage', 'perSecond']),        'ion damage / second')}
      {renderAttribute(weapon, R.path(['disruptionDamage', 'perSecond']), 'disruption damage / second')}
      {renderAttribute(weapon, R.path(['slowingDamage', 'perSecond']),    'slowing damage / second')}
      {renderAttribute(weapon, R.path(['firingEnergy', 'perSecond']),     'firing energy / second')}
      {renderAttribute(weapon, R.path(['firingHeat', 'perSecond']),       'firing heat / second')}
      {renderAttribute(weapon, R.path(['firingFuel', 'perSecond']),       'firing fuel / second')}
      {renderAttribute(weapon, R.prop('shotsPerSecond'),                  'shots / second')}
      {renderAttribute(weapon, R.prop('turretTurn'),                      'turret turn rate')}

      {R.has('shotsPerSecond', weapon) && (typeof weapon.shotsPerSecond === 'number') && <br />}
      {renderAttribute(weapon, R.path(['shieldDamage', 'perShot']),     'shield damage / shot')}
      {renderAttribute(weapon, R.path(['hullDamage', 'perShot']),       'hull damage / shot')}
      {renderAttribute(weapon, R.path(['heatDamage', 'perShot']),       'heat damage / shot')}
      {renderAttribute(weapon, R.path(['ionDamage', 'perShot']),        'ion damage / shot')}
      {renderAttribute(weapon, R.path(['disruptionDamage', 'perShot']), 'disruption damage / shot')}
      {renderAttribute(weapon, R.path(['slowingDamage', 'perShot']),    'slowing damage / shot')}
      {renderAttribute(weapon, R.path(['firingEnergy', 'perShot']),     'firing energy / shot')}
      {renderAttribute(weapon, R.path(['firingHeat', 'perShot']),       'firing heat / shot')}
      {renderAttribute(weapon, R.prop('inaccuracy'),                    'inaccuracy')}
      {renderAttribute(weapon, R.prop('antiMissile'),                   'anti-missile')}
    </div>
  );
};

const imageURL = (outfit) => {
  const filename = window.encodeURI(outfit.thumbnail) + ".png";

  return "https://raw.githubusercontent.com/endless-sky/endless-sky/master/images/" + filename;
};

const ShipInstallation = ({ shipName, shipModification, quantity }) => {
  let link;

  if (R.isNil(shipModification)) {
    link = <ShipLink shipName={shipName} />;
  } else {
    link = <ShipModificationLink shipName={shipName} shipModification={shipModification} />;
  }

  if (quantity === 1) {
    return <li className="list-group-item">{link}</li>;
  } else {
    return <li className="list-group-item"><span className="badge">{quantity}</span>{link}</li>;
  }
};

const InstallationsList = ({ installations }) => (
  <Panel.Body>
    <ul className="list-group">
      {installations.map(installation => <ShipInstallation key={R.values(installation)} {...installation} />)}
    </ul>
  </Panel.Body>
);

const PlanetsList = ({ planets }) => (
  <Panel.Body>
    <ul className="list-group">
      {planets.map(({ name, system }) => <li className="list-group-item" key={name}>{name} <Label>{system}</Label></li>)}
    </ul>
  </Panel.Body>
);

const OutfitPage = ({ outfit, shipInstallations, sellingPlanets }) => (
  <div className="app">
    <Row>
      <Col md={12}>
        <Panel>
          <Panel.Heading>{outfit.name}</Panel.Heading>

          <Panel.Body>
            <div className="media">
              <div className="media-body">
                <Row>
                  <Col md={4}>
                    <OutfitDescription description={outfit.description} />

                    {outfit.licenses.length > 0 && <span><br /><br /><p className="italic">Requires a {outfit.licenses[0]} license.</p></span>}
                  </Col>

                  <Col md={4}>
                    <ul>
                      {renderAttribute(outfit, R.prop('category'),       'category')}
                      {renderAttribute(outfit, R.prop('cost'),           'cost')}
                      {renderAttribute(outfit, R.prop('outfitSpace'),    'outfit space needed')}
                      {renderAttribute(outfit, R.prop('weaponCapacity'), 'weapon capacity needed')}
                      {renderAttribute(outfit, R.prop('engineCapacity'), 'engine capacity needed')}
                    </ul>
                  </Col>

                  <Col md={4}>
                    {outfit.hyperdrive && <p className="italic">Allows you to make hyperjumps.</p>}
                    {outfit.jumpDrive && <p className="italic">Lets you jump to any nearby system.</p>}
                    {(outfit.installable === -1) && <p className="italic">This is not an installable item.</p>}

                    <ul>
                      {renderAttribute(outfit, R.prop('mass'),                   'mass')}
                      {renderAttribute(outfit, R.prop('thrust'),                 'thrust')}
                      {renderAttribute(outfit, R.prop('thrustingEnergy'),        'thrusting energy')}
                      {renderAttribute(outfit, R.prop('thrustingHeat'),          'thrusting heat')}
                      {renderAttribute(outfit, R.prop('turn'),                   'turn')}
                      {renderAttribute(outfit, R.prop('turningEnergy'),          'turning energy')}
                      {renderAttribute(outfit, R.prop('turningHeat'),            'turning heat')}
                      {renderAttribute(outfit, R.prop('afterburnerEnergy'),      'afterburner energy')}
                      {renderAttribute(outfit, R.prop('afterburnerFuel'),        'afterburner fuel')}
                      {renderAttribute(outfit, R.prop('afterburnerHeat'),        'afterburner heat')}
                      {renderAttribute(outfit, R.prop('afterburnerThrust'),      'afterburner thrust')}
                      {renderAttribute(outfit, R.prop('reverseThrust'),          'reverse thrust')}
                      {renderAttribute(outfit, R.prop('reverseThrustingEnergy'), 'reverse thrusting energy')}
                      {renderAttribute(outfit, R.prop('reverseThrustingHeat'),   'reverse thrusting heat')}
                      {renderAttribute(outfit, R.prop('energyGeneration'),       'energy generation')}
                      {renderAttribute(outfit, R.prop('solarCollection'),        'solar collection')}
                      {renderAttribute(outfit, R.prop('energyCapacity'),         'energy capacity')}
                      {renderAttribute(outfit, R.prop('energyConsumption'),      'energy consumption')}
                      {renderAttribute(outfit, R.prop('heatGeneration'),         'heat generation')}
                      {renderAttribute(outfit, R.prop('cooling'),                'cooling')}
                      {renderAttribute(outfit, R.prop('activeCooling'),          'active cooling')}
                      {renderAttribute(outfit, R.prop('coolingEnergy'),          'cooling energy')}
                      {renderAttribute(outfit, R.prop('hullRepairRate'),         'hull repair rate')}
                      {renderAttribute(outfit, R.prop('hullEnergy'),             'hull energy')}
                      {renderAttribute(outfit, R.prop('hullHeat'),               'hull heat')}
                      {renderAttribute(outfit, R.prop('shieldGeneration'),       'shield generation')}
                      {renderAttribute(outfit, R.prop('shieldEnergy'),           'shield energy')}
                      {renderAttribute(outfit, R.prop('shieldHeat'),             'shield heat')}
                      {renderAttribute(outfit, R.prop('ramscoop'),               'ramscoop')}
                      {renderAttribute(outfit, R.prop('requiredCrew'),           'required crew')}
                      {renderAttribute(outfit, R.prop('captureAttack'),          'capture attack')}
                      {renderAttribute(outfit, R.prop('captureDefense'),         'capture defense')}
                      {renderAttribute(outfit, R.prop('illegal'),                'illegal')}
                      {renderAttribute(outfit, R.prop('cargoSpace'),             'cargo space')}
                      {renderAttribute(outfit, R.prop('coolingInefficiency'),    'cooling inefficiency')}
                      {renderAttribute(outfit, R.prop('heatDissipation'),        'heat dissipation')}
                      {renderAttribute(outfit, R.prop('fuelCapacity'),           'fuel capacity')}
                      {renderAttribute(outfit, R.prop('jumpFuel'),               'jump fuel')}
                      {renderAttribute(outfit, R.prop('jumpSpeed'),              'jump speed')}
                      {renderAttribute(outfit, R.prop('scramDrive'),             'scram drive')}
                      {renderAttribute(outfit, R.prop('atmosphereScan'),         'atmosphere scan')}
                      {renderAttribute(outfit, R.prop('cargoScanPower'),         'cargo scan power')}
                      {renderAttribute(outfit, R.prop('cargoScanSpeed'),         'cargo scan speed')}
                      {renderAttribute(outfit, R.prop('outfitScanPower'),        'outfit scan power')}
                      {renderAttribute(outfit, R.prop('outfitScanSpeed'),        'outfit scan speed')}
                      {renderAttribute(outfit, R.prop('scanInterference'),       'scan interference')}
                      {renderAttribute(outfit, R.prop('radarJamming'),           'radar jamming')}
                      {renderAttribute(outfit, R.prop('cloak'),                  'cloak')}
                      {renderAttribute(outfit, R.prop('cloakingEnergy'),         'cloaking energy')}
                      {renderAttribute(outfit, R.prop('cloakingFuel'),           'cloaking fuel')}
                      {renderAttribute(outfit, R.prop('bunks'),                  'bunks')}
                      {renderAttribute(outfit, R.prop('automaton'),              'automaton')}
                      {renderAttribute(outfit, R.prop('quantumKeystone'),        'quantum keystone')}
                      {renderAttribute(outfit, R.prop('map'),                    'map')}

                      <AmmoItem object={outfit} />
                      {renderAttribute(outfit, R.prop('gatlingRoundCapacity'), 'gatling round capacity')}
                      {renderAttribute(outfit, R.prop('javelinCapacity'),      'javelin capacity')}
                      {renderAttribute(outfit, R.prop('finisherCapacity'),     'finisher capacity')}
                      {renderAttribute(outfit, R.prop('trackerCapacity'),      'tracker capacity')}
                      {renderAttribute(outfit, R.prop('rocketCapacity'),       'rocket capacity')}
                      {renderAttribute(outfit, R.prop('minelayerCapacity'),    'minelayer capacity')}
                      {renderAttribute(outfit, R.prop('piercerCapacity'),      'piercer capacity')}
                      {renderAttribute(outfit, R.prop('meteorCapacity'),       'meteor capacity')}
                      {renderAttribute(outfit, R.prop('railgunSlugCapacity'),  'railgun slug capacity')}
                      {renderAttribute(outfit, R.prop('sidewinderCapacity'),   'sidewinder capacity')}
                      {renderAttribute(outfit, R.prop('thunderheadCapacity'),  'thunderhead capacity')}
                      {renderAttribute(outfit, R.prop('torpedoCapacity'),      'torpedo capacityn')}
                      {renderAttribute(outfit, R.prop('typhoonCapacity'),      'typhoon capacity')}

                      {outfit.weapon && <WeaponAttributes weapon={outfit.weapon} />}
                    </ul>

                    {outfit.unplunderable && <p className="italic">This outfit cannot be plundered.</p>}
                  </Col>
                </Row>
              </div>

              <div className="media-right">
                {outfit.thumbnail && <Image src={imageURL(outfit)} />}
              </div>
            </div>
          </Panel.Body>
        </Panel>
      </Col>
    </Row>

    <Row>
      <Col md={6}>
        <Panel>
          <Panel.Heading>Installed on {shipInstallations.length} ships</Panel.Heading>

          {shipInstallations.length > 0 && <InstallationsList installations={shipInstallations} />}
        </Panel>
      </Col>

      <Col md={6}>
        <Panel>
          <Panel.Heading>Sold at {sellingPlanets.length} planets</Panel.Heading>

          {sellingPlanets.length > 0 && <PlanetsList planets={sellingPlanets} />}
        </Panel>
      </Col>
    </Row>
  </div>
);

const findShipsWithOutfit = (ships, outfit) => {
  return ships.map(ship => {
    const shipOutfit = R.or(ship.outfits, []).find(
      ({ name }) => name === outfit.name
    );

    return {
      shipName: ship.name,
      shipModification: ship.modification,
      quantity: shipOutfit ? shipOutfit.quantity : 0
    };
  }).filter(({ quantity }) => quantity > 0);
};

const mapStateToProps = (state, { match: { params: { outfitName } } }) => {
  const outfit = state.outfits.find(outfit => kebabCase(outfit.name) === outfitName);
  const shipInstallations = findShipsWithOutfit(R.concat(state.ships, state.shipModifications), outfit);

  const outfitters = state.outfitters.filter(outfitter => R.contains(outfit.name, outfitter.outfits));
  const sellingPlanets = R.pipe(
    R.map(R.prop('planets')),
    R.flatten,
    R.uniq,
    R.sortBy(R.prop('name'))
  )(outfitters);

  return { outfit, shipInstallations, sellingPlanets };
};

export default connect(mapStateToProps)(OutfitPage);
