import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Grid, Row, Col, Panel, Image } from 'react-bootstrap';
import { FormattedNumber } from './common';
import './ShipPage.css';

function intersperse(arr, sep) {
  if (arr.length === 0) {
    return [];
  } else {
    return arr.slice(1).reduce((xs, x, idx) => xs.concat([sep(idx), x]), [arr[0]]);
  }
}

class ShipDescription extends Component {
  render() {
    return (
      <Row>
        <Col md={12}>
          <div className="well">
            {intersperse(this.props.description, (idx) => <span key={idx}><br/><br/></span>)}
          </div>
        </Col>
      </Row>
    );
  }
}

class ShipPage extends Component {
  licenses() {
    if (this.props.ship.licenses.length === 2) {
      return (<p className="licenses">This ship requires {this.props.ship.licenses[0]} and {this.props.ship.licenses[1]} licenses.</p>);
    } else {
      return (<p className="licenses">This ship requires a {this.props.ship.licenses[0]} license.</p>);
    }
  }

  imageURL() {
    var filename;

    if (this.props.ship.name === "Shuttle") {
      // for some crazy reason Shuttle has different sprite filenames
      filename = "ship/shuttle=0.png";
    } else if (this.props.ship.sprite[1]) {
      filename = window.encodeURI(this.props.ship.sprite[0]) + "-0.png";
    } else {
      filename = window.encodeURI(this.props.ship.sprite[0]) + ".png";
    }

    // probably not a good idea to hotlink to github to the master branch
    return "https://raw.githubusercontent.com/endless-sky/endless-sky/master/images/" + filename;
  }

  formatDescription() {
    return this.props.ship.description.map((line, index) => (
      <p key={`${this.props.ship.name}-description-${index}`}>{line}</p>
    ));
  }

  outfitItems() {
    return this.props.ship.outfits.map(({ name, quantity }) => {
      var key = `${this.props.ship.name}-${name}`;

      if (quantity === 1) {
        return (
          <li className="list-group-item" key={key}>
            {name}
          </li>
        );
      } else {
        return (
          <li className="list-group-item" key={key}>
            <span className="badge">{quantity}</span>
            {name}
          </li>
        );
      }
    });
  }

  render() {
    console.log(this.props.ship);

    return (
      <div className="app">
        <ol className="breadcrumb">
          <li><Link to="/">Ships</Link></li>
          <li className="active">{this.props.ship.name}</li>
        </ol>

        <Grid>
          <Row>
            <Col md={6}>
              <Panel>
                <Panel.Heading>{this.props.ship.name}</Panel.Heading>

                <Panel.Body>
                  <div className="media">
                    <div className="media-body">
                      <ul>
                        <li>cost: <FormattedNumber number={this.props.ship.cost} /></li>
                        <li>shields: <FormattedNumber number={this.props.ship.shields} /></li>
                        <li>hull: <FormattedNumber number={this.props.ship.hull} /></li>
                        <li>mass: <FormattedNumber number={this.props.ship.mass} /></li>
                        <li>cargo space: <FormattedNumber number={this.props.ship.cargoSpace} /></li>
                        <li>required crew: <FormattedNumber number={this.props.ship.requiredCrew} /></li>
                        <li>bunks: <FormattedNumber number={this.props.ship.bunks} /></li>
                        <li>fuel capacity: <FormattedNumber number={this.props.ship.fuelCapacity} /></li>
                        <li>outfit space: <FormattedNumber number={this.props.ship.outfitSpace} /></li>
                        <li>weapon capacity: <FormattedNumber number={this.props.ship.weaponCapacity} /></li>
                        <li>engine capacity: <FormattedNumber number={this.props.ship.engineCapacity} /></li>
                        <li>guns: <FormattedNumber number={this.props.ship.guns} /></li>
                        <li>turrets: <FormattedNumber number={this.props.ship.turrets} /></li>
                        {this.props.ship.drones > 0 && <li>drones: <FormattedNumber number={this.props.ship.drones} /></li>}
                        {this.props.ship.fighters > 0 && <li>fighters: <FormattedNumber number={this.props.ship.fighters} /></li>}
                      </ul>

                      {this.props.ship.licenses.length > 0 && this.licenses()}
                    </div>

                    <div className="media-right">
                      <Image src={this.imageURL()} className="ship-sprite" />
                    </div>
                  </div>
                </Panel.Body>
              </Panel>
            </Col>

            <Col md={6}>
              <Panel>
                <Panel.Heading>Default outfits</Panel.Heading>

                <Panel.Body>
                  <ul className="list-group">
                    {this.outfitItems()}
                  </ul>
                </Panel.Body>
              </Panel>
            </Col>
          </Row>

          {this.props.ship.description.length > 0 && <ShipDescription description={this.props.ship.description}/>}
        </Grid>
      </div>
    );
  }
}

export default ShipPage;
