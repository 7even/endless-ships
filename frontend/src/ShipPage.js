import React from 'react';
import { Link } from 'react-router-dom';
import { Grid, Row, Col, Panel, Image } from 'react-bootstrap';
import NumberFormat from 'react-number-format';
import './ShipPage.css';

function FormattedNumber(props) {
  return (
    <NumberFormat value={props.number}
                  displayType={'text'}
                  thousandSeparator={true} />
  );
}

const ShipPage = ({ ship }) => {
  console.log(ship);
  return (
    <div className="app">
      <ol className="breadcrumb">
        <li><Link to="/">Ships</Link></li>
        <li className="active">{ship.name}</li>
      </ol>

      <Grid>
        <Row>
          <Col md={6}>
            <Panel>
              <Panel.Heading>{ship.name}</Panel.Heading>

              <Panel.Body>
                <div className="media">
                  <div className="media-body">
                    <ul>
                      <li>cost: <FormattedNumber number={ship.cost} /></li>
                      <li>shields: <FormattedNumber number={ship.shields} /></li>
                      <li>hull: <FormattedNumber number={ship.hull} /></li>
                      <li>mass: <FormattedNumber number={ship.mass} /></li>
                      <li>cargo space: <FormattedNumber number={ship.cargoSpace} /></li>
                      <li>required crew: <FormattedNumber number={ship.requiredCrew} /></li>
                      <li>bunks: <FormattedNumber number={ship.bunks} /></li>
                      <li>fuel capacity: <FormattedNumber number={ship.fuelCapacity} /></li>
                      <li>outfit space: <FormattedNumber number={ship.outfitSpace} /></li>
                      <li>weapon capacity: <FormattedNumber number={ship.weaponCapacity} /></li>
                      <li>engine capacity: <FormattedNumber number={ship.engineCapacity} /></li>
                    </ul>
                  </div>

                  <div className="media-right">
                    <Image src="https://raw.githubusercontent.com/endless-sky/endless-sky/master/images/ship/bactrian.png?raw=true" className="ship-sprite" />
                  </div>
                </div>
              </Panel.Body>
            </Panel>
          </Col>
        </Row>

        <Row>
          <Col md={12}>
            <Panel>
              <Panel.Body>{ship.description.map((line, index) => (<p key={`${ship.name}-description-${index}`}>{line}</p>))}</Panel.Body>
            </Panel>
          </Col>
        </Row>
      </Grid>
    </div>
  );
};

export default ShipPage;
