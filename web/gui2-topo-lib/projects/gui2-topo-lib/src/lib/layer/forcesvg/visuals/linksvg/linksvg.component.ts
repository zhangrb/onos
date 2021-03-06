/*
 * Copyright 2018-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import {
    ChangeDetectorRef,
    Component, EventEmitter,
    Input, OnChanges, Output, SimpleChanges,
} from '@angular/core';
import {Link, LinkHighlight, UiElement} from '../../models';
import {LogService} from 'gui2-fw-lib';
import {NodeVisual, SelectedEvent} from '../nodevisual';
import {animate, state, style, transition, trigger} from '@angular/animations';

interface Point {
    x: number;
    y: number;
}

@Component({
    selector: '[onos-linksvg]',
    templateUrl: './linksvg.component.html',
    styleUrls: ['./linksvg.component.css'],
    animations: [
        trigger('linkLabelVisible', [
            state('true', style( {
                opacity: 1.0,
            })),
            state( 'false', style({
                opacity: 0
            })),
            transition('false => true', animate('500ms ease-in')),
            transition('true => false', animate('1000ms ease-out'))
        ])
    ]
})
export class LinkSvgComponent extends NodeVisual implements OnChanges {
    @Input() link: Link;
    @Input() highlighted: string = '';
    @Input() highlightsEnabled: boolean = true;
    @Input() label: string;
    @Input() scale = 1.0;
    isHighlighted: boolean = false;
    @Output() selectedEvent = new EventEmitter<SelectedEvent>();
    @Output() enhancedEvent = new EventEmitter<Link>();
    enhanced: boolean = false;
    labelPosSrc: Point = {x: 0, y: 0};
    labelPosTgt: Point = {x: 0, y: 0};

    constructor(
        protected log: LogService,
        private ref: ChangeDetectorRef
    ) {
        super();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['linkHighlight']) {
            const hl: LinkHighlight = changes['linkHighlight'].currentValue;
            this.highlighted = hl.css;
            this.label = hl.label;
            this.isHighlighted = true;
            setTimeout(() => {
                this.isHighlighted = false;
                this.highlighted = '';
                this.ref.markForCheck();
            }, 4990);

        }

        this.ref.markForCheck();
    }

    enhance() {
        if (!this.highlightsEnabled) {
            return;
        }
        this.enhancedEvent.emit(this.link);
        this.enhanced = true;
        this.repositionLabels();
        setTimeout(() => {
            this.enhanced = false;
            this.ref.markForCheck();
        }, 1000);
    }

    /**
     * We want to place the label for the port about 40 px from the node.
     * If the distance between the nodes is less than 100, then just place the
     * label 1/3 of the way from the node
     */
    repositionLabels(): void {
        const x1: number = this.link.source.x;
        const y1: number = this.link.source.y;
        const x2: number = this.link.target.x;
        const y2: number = this.link.target.y;

        const dist = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        const offset = dist > 100 ? 40 : dist / 3;
        this.labelPosSrc = <Point>{
            x: x1 + (x2 - x1) * offset / dist,
            y: y1 + (y2 - y1) * offset / dist
        };

        this.labelPosTgt = <Point>{
            x: x2 - (x2 - x1) * offset / dist,
            y: y2 - (y2 - y1) * offset / dist
        };
    }

    /**
     * For the 14pt font we are using, the average width seems to be about 8px
     * @param text The string we want to calculate a width for
     */
    textLength(text: string) {
        return text.length * 8;
    }
}
