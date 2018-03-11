import React from 'react';
import { connect } from 'react-redux';
import { Row, Col, Panel, Image } from 'react-bootstrap';
import { FormattedNumber, kebabCase, intersperse } from '../common';

const OutfitDescription = ({ description }) => {
  return intersperse(description, index => <span key={index}><br/><br/></span>);
};

const imageURL = (outfit) => {
  const filename = window.encodeURI(outfit.thumbnail) + ".png";

  return "https://raw.githubusercontent.com/endless-sky/endless-sky/master/images/" + filename;
};

const OutfitPage = ({ outfit }) => (
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
                  </Col>

                  <Col md={4}>
                    <ul>
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
  </div>
);

const mapStateToProps = (state, { match: { params: { outfitName } } }) => {
  return {
    outfit: state.outfits.find(outfit => kebabCase(outfit.name) === outfitName)
  };
};

export default connect(mapStateToProps)(OutfitPage);
