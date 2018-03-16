import React from 'react';
import { connect } from 'react-redux';
import { Row, Col, Panel, Image } from 'react-bootstrap';
import { FormattedNumber, kebabCase, ShipLink, intersperse } from '../common';

const OutfitDescription = ({ description }) => {
  return intersperse(description, index => <span key={index}><br/><br/></span>);
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

                    {outfit.licenses.length > 0 && <span><br /><br /><p className="licenses">Requires a {outfit.licenses[0]} license.</p></span>}
                  </Col>

                  <Col md={4}>
                    <ul>
                      <li>category: {outfit.category}</li>
                      <li>cost: <FormattedNumber number={outfit.cost} /></li>
                      <li>outfit space needed: <FormattedNumber number={outfit.outfitSpace} /></li>
                      {outfit.weaponCapacity && <li>weapon capacity needed: <FormattedNumber number={outfit.weaponCapacity} /></li>}
                      {outfit.engineCapacity && <li>engine capacity needed: <FormattedNumber number={outfit.engineCapacity} /></li>}
                    </ul>
                  </Col>

                  <Col md={4}>
                    <ul>
                      {outfit.thrust && <li>thrust: <FormattedNumber number={outfit.thrust} /></li>}
                      {outfit.thrustingEnergy && <li>thrusting energy: <FormattedNumber number={outfit.thrustingEnergy} /></li>}
                      {outfit.thrustingHeat && <li>thrusting heat: <FormattedNumber number={outfit.thrustingHeat} /></li>}
                      {outfit.turn && <li>turn: <FormattedNumber number={outfit.turn} /></li>}
                      {outfit.turningEnergy && <li>turning energy: <FormattedNumber number={outfit.turningEnergy} /></li>}
                      {outfit.turningHeat && <li>turning heat: <FormattedNumber number={outfit.turningHeat} /></li>}
                      {outfit.afterburnerFuel && <li>afterburner fuel: <FormattedNumber number={outfit.afterburnerFuel} /></li>}
                      {outfit.afterburnerHeat && <li>afterburner heat: <FormattedNumber number={outfit.afterburnerHeat} /></li>}
                      {outfit.afterburnerThrust && <li>afterburner thrust: <FormattedNumber number={outfit.afterburnerThrust} /></li>}
                      {outfit.reverseThrust && <li>reverse thrust: <FormattedNumber number={outfit.reverseThrust} /></li>}
                      {outfit.reverseThrustingEnergy && <li>reverse thrusting energy: <FormattedNumber number={outfit.reverseThrustingEnergy} /></li>}
                      {outfit.reverseThrustingHeat && <li>reverse thrusting heat: <FormattedNumber number={outfit.reverseThrustingHeat} /></li>}
                      {outfit.energyGeneration && <li>energy generation: <FormattedNumber number={outfit.energyGeneration} /></li>}
                      {outfit.solarCollection && <li>solar collection: <FormattedNumber number={outfit.solarCollection} /></li>}
                      {outfit.energyCapacity && <li>energy capacity: <FormattedNumber number={outfit.energyCapacity} /></li>}
                      {outfit.energyConsumption && <li>energy consumption: <FormattedNumber number={outfit.energyConsumption} /></li>}
                      {outfit.heatGeneration && <li>heat generation: <FormattedNumber number={outfit.heatGeneration} /></li>}
                      {outfit.cooling && <li>cooling: <FormattedNumber number={outfit.cooling} /></li>}
                      {outfit.activeCooling && <li>active cooling: <FormattedNumber number={outfit.activeCooling} /></li>}
                      {outfit.coolingEnergy && <li>cooling energy: <FormattedNumber number={outfit.coolingEnergy} /></li>}
                      {outfit.hullRepairRate && <li>hull repair rate: <FormattedNumber number={outfit.hullRepairRate} /></li>}
                      {outfit.hullEnergy && <li>hull energy: <FormattedNumber number={outfit.hullEnergy} /></li>}
                      {outfit.hullHeat && <li>hull heat: <FormattedNumber number={outfit.hullHeat} /></li>}
                      {outfit.shieldGeneration && <li>shield generation: <FormattedNumber number={outfit.shieldGeneration} /></li>}
                      {outfit.shieldEnergy && <li>shield energy: <FormattedNumber number={outfit.shieldEnergy} /></li>}
                      {outfit.shieldHeat && <li>shield heat: <FormattedNumber number={outfit.shieldHeat} /></li>}
                      {outfit.ramscoop && <li>ramscoop: <FormattedNumber number={outfit.ramscoop} /></li>}
                    </ul>
                  </Col>
                </Row>
              </div>

              <div className="media-right">
                <Image src={imageURL(outfit)} />
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
