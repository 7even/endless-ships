import React from 'react';
import { connect } from 'react-redux';
import { Row, Col, Panel, Image } from 'react-bootstrap';
import R from 'ramda';
import { FormattedNumber, kebabCase, ShipLink, OutfitLink, intersperse } from '../common';

const OutfitDescription = ({ description }) => {
  if (description.length === 0) {
    return <p className="italic">No description.</p>;
  } else {
    return intersperse(description, index => <span key={index}><br/><br/></span>);
  }
};

const renderAttribute = (object, prop, label) => {
  const value = prop(object);

  if (!R.isNil(value)) {
    if (typeof value === 'number') {
      return <li>{label}: <FormattedNumber number={value} /></li>;
    } else {
      return <li>{label}: {value}</li>;
    }
  } else {
    return null;
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

const ShipInstallation = ({ shipName, quantity }) => {
  if (quantity === 1) {
    return <li className="list-group-item"><ShipLink shipName={shipName} /></li>;
  } else {
    return (
      <li className="list-group-item">
        <span className="badge">{quantity}</span>
        <ShipLink shipName={shipName} />
      </li>
    );
  }
};

const InstallationsList = ({ installations }) => (
  <Panel.Body>
    <ul className="list-group">
      {installations.map(installation => <ShipInstallation key={installation.shipName} {...installation} />)}
    </ul>
  </Panel.Body>
);

const OutfitPage = ({ outfit, shipInstallations }) => (
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
    </Row>
  </div>
);

const mapStateToProps = (state, { match: { params: { outfitName } } }) => {
  const outfit = state.outfits.find(outfit => kebabCase(outfit.name) === outfitName);

  const shipInstallations = state.ships.map(ship => {
    const shipOutfit = ship.outfits.find(
      ({ name }) => name === outfit.name
    );

    return {
      shipName: ship.name,
      quantity: shipOutfit ? shipOutfit.quantity : 0
    };
  }).filter(({ quantity }) => quantity > 0);

  return { outfit, shipInstallations };
};

export default connect(mapStateToProps)(OutfitPage);
